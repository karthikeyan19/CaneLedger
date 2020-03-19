package com.karthikeyan.myapplication.activity

import android.app.DatePickerDialog
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.karthikeyan.myapplication.R
import com.karthikeyan.myapplication.adapter.CustomerSalesListAdapter
import com.karthikeyan.myapplication.dataInterface.DataBaseHelper
import kotlinx.android.synthetic.main.activity_customer_sales_view.*
import kotlinx.android.synthetic.main.content_customer_sales_view.*
import java.text.SimpleDateFormat
import java.util.*

import java.time.LocalDate
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import com.karthikeyan.myapplication.dataInterface.SaleEntry
import kotlinx.android.synthetic.main.activity_customer_sales_view.showButton
import kotlinx.android.synthetic.main.activity_customer_sales_view.showSelectionRG
import kotlinx.android.synthetic.main.activity_customer_sales_view.toolbar
import kotlinx.android.synthetic.main.dialog_get_dates.view.*
import kotlinx.android.synthetic.main.item_customer_profile.*
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.lang.Exception
import java.text.DateFormat
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class CustomerSalesViewActivity : AppCompatActivity() {
    lateinit var dbHelper:DataBaseHelper
    var isExpand=true
    var adapter: CustomerSalesListAdapter? =null
    var cid:Int=0
    var cname:String =""
    var balEmpty:Int = 0
    var balAmt:Double = 0.0
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_sales_view)
        setSupportActionBar(toolbar)
        custmerSaleEntryRV.layoutManager =  LinearLayoutManager(this)
        dbHelper= DataBaseHelper(this)
        val i = intent
        if(i.extras!=null) {

            cid = i.extras.get("CID").toString().toInt()
            cname = i.extras.get("CNAME").toString()
            balEmpty = i.extras.get("EMPTY").toString().toInt()
            balAmt = i.extras.get("AMT").toString().toDouble()


            setSupportActionBar(toolbar)
            val text1 = cname
            Log.d("ss",cid.toString())
            val text2 = " BAL EMPTY:"+balEmpty.toString()+" BALANCE: ₹"+balAmt.toString()



// let's put both spans together with a separator and all
            supportActionBar!!.title =text1
            balanceCusTV.text = text2
            val dates = getCurrentMonth()
            showButton.text=SimpleDateFormat("MMM - yyyy").format(dates[0].time)

            adapter = CustomerSalesListAdapter(dbHelper.getSalesDetails(cid,dates[0],dates[1]), this)

            custmerSaleEntryRV.adapter=adapter

            showButton.setOnClickListener{view ->
                if(isExpand){
                    showSelectionRG.visibility= View.VISIBLE
                    showButton.setCompoundDrawablesWithIntrinsicBounds(null,null,this.resources.getDrawable(R.drawable.ic_baseline_keyboard_arrow_down_white_48dp,null),null)

                }else{
                    showSelectionRG.visibility=View.GONE
                    showButton.setCompoundDrawablesWithIntrinsicBounds(null,null,this.getDrawable(R.drawable.ic_baseline_keyboard_arrow_right_white_48dp),null)

                }
                isExpand=!isExpand
            }
            showSelectionRG.check(R.id.curMonthRB)
            showSelectionRG.setOnCheckedChangeListener{rg,checkedId->  val sd = SimpleDateFormat("MMM - yyyy")
                Log.d("dd","checkId:"+ checkedId +(checkedId==R.id.prevMonthRB))
                when(checkedId){
                    R.id.curMonthRB -> {

                        val cdates = getCurrentMonth()
                        adapter!!.addcustomerSalesList(dbHelper.getSalesDetails(cid,cdates[0],cdates[1]))
                        showButton.text=sd.format(cdates[0].time)
                    }
                    R.id.prevMonthRB -> {
                        val pdates = getPrevMonth()
                        adapter!!.addcustomerSalesList(dbHelper.getSalesDetails(cid,pdates[0],pdates[1]))
                        showButton.text=sd.format(pdates[0].time)

                    }
                    R.id.cusDateRB->{
                        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_get_dates,null)
                        val cal :Calendar = Calendar.getInstance()
                        val fdateSetListener = object : DatePickerDialog.OnDateSetListener {
                            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                                   dayOfMonth: Int) {

                                cal.set(Calendar.YEAR, year)
                                cal.set(Calendar.MONTH, monthOfYear)
                                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                updateDateInView(dialogView.fromDateET,cal)
                            }
                        }
                        val tdateSetListener = object : DatePickerDialog.OnDateSetListener {
                            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                                   dayOfMonth: Int) {

                                cal.set(Calendar.YEAR, year)
                                cal.set(Calendar.MONTH, monthOfYear)
                                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                updateDateInView(dialogView.toDateET,cal)
                            }
                        }
                        dialogView.fromDateET!!.setOnClickListener(object : View.OnClickListener {
                            override fun onClick(view: View) {
                                DatePickerDialog(this@CustomerSalesViewActivity,
                                    fdateSetListener,
                                    // set DatePickerDialog to point to today's date when it loads up
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH)).show()
                            }

                        })
                        dialogView.toDateET!!.setOnClickListener(object : View.OnClickListener {
                            override fun onClick(view: View) {
                                DatePickerDialog(this@CustomerSalesViewActivity,
                                    tdateSetListener,
                                    // set DatePickerDialog to point to today's date when it loads up
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH)).show()
                            }

                        })
                        val mBulider = AlertDialog.Builder(this)

                        mBulider.setView(dialogView).setTitle("Choose the Dates")
                        mBulider.setPositiveButton("Fetch", { dialog, id->
                            Log.d("ss","clicked")
                            val sDates = getSearchDates(dialogView.fromDateET.text.toString(),dialogView.toDateET.text.toString())
                            adapter!!.addcustomerSalesList(dbHelper.getSalesDetails(cid,sDates[0],sDates[1]))
                            showButton.text = dialogView.fromDateET.text.toString() +" - " + dialogView.toDateET.text.toString()


                        }
                            )

                        mBulider.setNegativeButton(R.string.cancel_btn, { dialog, id->
                            dialog.dismiss()
                            showSelectionRG.check(R.id.curMonthRB)
                        })
                        mBulider.show()


                    }

                   R.id.allDateRB-> {
                       adapter!!.addcustomerSalesList(dbHelper.getSalesDetails(cid,null,null))
                       showButton.text="All Date"
                   }

                }}

        }
    }
    fun updateDateInView(dateEntryET:EditText, cal: Calendar) {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        dateEntryET!!.setText(sdf.format(cal.getTime()))
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentMonth():Array<Date>{
        val c = Calendar.getInstance()
        val currentdate = LocalDate.now()
        c.set(currentdate.year,currentdate.monthValue-1,currentdate.dayOfMonth)
        val lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH)
        val sd = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")


        val fd=sd.parse("01/"+(currentdate.month.value)+"/"+currentdate.year+" 00:00:00")
        Log.d("ss","01/"+(currentdate.month.value)+"/"+currentdate.year)

        val td = sd.parse(lastDay.toString()+  "/"+(currentdate.monthValue)+"/"+currentdate.year+ " 23:59:59")
        Log.d("ss",lastDay.toString()+  "/"+(currentdate.monthValue)+"/"+currentdate.year)

        return arrayOf(fd,td)
    }
    fun getSearchDates(from:String,to:String):Array<Date>{
        val sd = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        val fdate = sd.parse(from+ " 00:00:00")
        val tDate = sd.parse(to+" 23:59:59")
        return  arrayOf(fdate,tDate)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getPrevMonth():Array<Date>{
        val c =Calendar.getInstance()
        val currentDate = LocalDate.now()
        val sd = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        var fd: Date =Date();
        var td: Date =Date();
        if(currentDate.monthValue>1){

            c.set(currentDate.year,currentDate.monthValue-2,1)
            val lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH)

             fd=sd.parse("01/"+(currentDate.month.value-1)+"/"+currentDate.year+ " 00:00:00")
            Log.d("ss","01/"+(currentDate.month.value-1)+"/"+currentDate.year)

            td = sd.parse(lastDay.toString()+  "/"+(currentDate.monthValue-1)+"/"+currentDate.year+" 23:59:59")
            Log.d("ss",lastDay.toString()+  "/"+(currentDate.monthValue-1)+"/"+currentDate.year)

        }else{
            c.set(currentDate.year-1,12,1)
            val lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH)

            fd=sd.parse("01/"+(12)+"/"+(currentDate.year-1)+ " 00:00:00")
            Log.d("ss","01/"+(12)+"/"+(currentDate.year-1))

            td = sd.parse(lastDay.toString()+  "/"+(12)+"/"+(currentDate.year-1)+" 23:59:59")
            Log.d("ss",lastDay.toString()+  "/"+(12)+"/"+(currentDate.year-1))
        }



        return arrayOf(fd,td)

    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun onSelectDateChoice(view: View){
        val sd = SimpleDateFormat("MMM - yyyy")

        when(view.id){
            R.id.curMonthRB -> {

                val cdates = getCurrentMonth()
                adapter!!.addcustomerSalesList(dbHelper.getSalesDetails(cid,cdates[0],cdates[1]))
                showButton.text=sd.format(cdates[0].time)
                }
            R.id.prevMonthRB -> {
                val pdates = getPrevMonth()
                adapter!!.addcustomerSalesList(dbHelper.getSalesDetails(cid,pdates[0],pdates[1]))
                showButton.text=sd.format(pdates[0].time)

            }


        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

         super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_customer_sales_view,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)
        when(item!!.itemId){
            R.id.action_download->{
                DownloadData().execute(mapOf("cname" to cname, "balEmpty" to balEmpty,"balance" to balAmt,"sDate" to showButton.text.toString(),"list" to adapter!!.getList()))
                Toast.makeText(baseContext,"File Downloaded",Toast.LENGTH_LONG).show()


            }
        }
        return true
    }
    override fun onBackPressed() {
        super.onBackPressed()

        this.finish()
    }

    override fun onPause() {
        super.onPause()
        this.finish()
    }

    override fun onStop() {
        super.onStop()
        this.finish()
    }
    class DownloadData: AsyncTask<Map<String, Any>, Int, Long>() {
        override fun doInBackground(vararg parmas: Map<String, Any>?): Long {
            val df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

            /**First of all we check if the external storage of the device is available for writing.
             * Remember that the external storage is not necessarily the sd card. Very often it is
             * the device storage.
             */
            val param=parmas[0]
            val state = Environment.getExternalStorageState();
            if (!Environment.MEDIA_MOUNTED.equals(state)) {
                return -1
            }
            else {
                Log.d("ss","Inside download")
                //We use the Download directory for saving our .csv file.
                val exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (!exportDir.exists())
                {
                    exportDir.mkdirs();
                }


                var printWriter: PrintWriter? = null;
                try
                {
                    val cname=param!!.get("cname")
                    var fileName = cname.toString() +"_"+param.get("sDate")+".csv"
                    fileName = fileName.replace("/","_").replace(" ", "")
                    val file =  File(exportDir, fileName);
                    file.createNewFile();
                    printWriter = PrintWriter(FileWriter(file));


                    printWriter.println(cname.toString()+" SALES DETAILS - "+ " BALANCE EMPTY:"+param.get("balEmpty")+" BALANCE: ₹"+param.get("balance"));
                    printWriter.println("DATE,LOAD,EMPTY,AMOUNT(₹)")
                    for(saleEntry in param.get("list") as ArrayList<SaleEntry>){
                        printWriter.println(df.format(saleEntry.doe)+","+saleEntry.load+","+saleEntry.empty+","+saleEntry.amt)
                    }
                    Log.d("ss","FileName "+fileName)

                }

                catch(exc:Exception) {
                    //if there are any exceptions, return false
                    Log.d("ss","FileName "+exc.fillInStackTrace())
                    return -1;
                }
                finally {
                    if(printWriter != null) printWriter.close();
                }
                return 1;
        }
    }

        override fun onPostExecute(result: Long?) {
            super.onPostExecute(result)
            if(result!!.equals(1)){

            }
        }
//     fun exportDatabase() :Boolean{
//         val df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
//
//        /**First of all we check if the external storage of the device is available for writing.
//         * Remember that the external storage is not necessarily the sd card. Very often it is
//         * the device storage.
//         */
//         val state = Environment.getExternalStorageState();
//        if (!Environment.MEDIA_MOUNTED.equals(state)) {
//            return false;
//        }
//        else {
//            //We use the Download directory for saving our .csv file.
//            val exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//            if (!exportDir.exists())
//            {
//                exportDir.mkdirs();
//            }
//
//
//            var printWriter: PrintWriter? = null;
//            try
//            {
//                var fileName = cname+"_"+showButton.text+".csv"
//                fileName = fileName.replace("/","_")
//                val file =  File(exportDir, fileName);
//                file.createNewFile();
//                printWriter = PrintWriter(FileWriter(file));
//
//
//                printWriter.println(cname+" SALES DETAILS - "+ " BALANCE EMPTY:"+balEmpty.toString()+" BALANCE: ₹"+balAmt.toString());
//                printWriter.println("DATE,LOAD,EMPTY,AMOUNT(₹)")
//                for(saleEntry in adapter!!.getList()){
//                    printWriter.println(df.format(saleEntry.doe)+","+saleEntry.load+","+saleEntry.empty+","+saleEntry.amt)
//                }
//            }
//
//            catch(exc:Exception) {
//                //if there are any exceptions, return false
//                return false;
//            }
//            finally {
//                if(printWriter != null) printWriter.close();
//            }
//
//            //If there are no errors, return true.
//            return true;
//        }
    }
}




