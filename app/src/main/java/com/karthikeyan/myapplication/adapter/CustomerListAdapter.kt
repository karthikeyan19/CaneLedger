package com.karthikeyan.myapplication.adapter

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import com.karthikeyan.myapplication.activity.CustomerSalesViewActivity
import com.karthikeyan.myapplication.R
import com.karthikeyan.myapplication.dataInterface.CustomerProfile
import com.karthikeyan.myapplication.dataInterface.DataBaseHelper
import com.karthikeyan.myapplication.dataInterface.SaleEntry
import kotlinx.android.synthetic.main.dialog_update_sale.view.*
import kotlinx.android.synthetic.main.item_customer_profile.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

open class CustomerListAdapter(val customerList:ArrayList<CustomerProfile>,val context: Context):RecyclerView.Adapter<CustomerListAdapter.ViewHolder>(){
    lateinit var dbHelper: DataBaseHelper

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        dbHelper=DataBaseHelper(context)
       return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_customer_profile,p0,false))
    }

    override fun getItemCount(): Int {
     return customerList.size
    }
    fun addCustomerList(cusList:List<CustomerProfile> ){
        customerList.clear()
        customerList.addAll(cusList)
        notifyDataSetChanged()
    }
    fun addCustomer(cus:CustomerProfile,pos:Int){

        customerList.add(cus)
        notifyItemChanged(pos)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(item: ViewHolder, pos: Int) {
         var profile = customerList.get(pos)
        item.customerNameTV.text = profile.cname
      updateBalance(item,profile.balAmt,profile.balEmpty)

        item.viewBtn.setOnClickListener{view->
           val i = Intent(context, CustomerSalesViewActivity::class.java)
            i.putExtra("CID",profile.cid)
            i.putExtra("CNAME",profile.cname)
            i.putExtra("EMPTY",profile.balEmpty)
            i.putExtra("AMT",profile.balAmt)
            context.startActivity(i)

        }
        item.editBtn.setOnClickListener{view ->
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update_sale,null)
            val cal :Calendar = Calendar.getInstance()
            fun updateDateInView() {
                val myFormat = "dd/MM/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                dialogView.dateOfEntryET!!.setText(sdf.format(cal.getTime()))
            }
            val dateSetListener = object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                       dayOfMonth: Int) {

                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    updateDateInView()
                }
            }

            dialogView.dateOfEntryET!!.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    DatePickerDialog(context,
                        dateSetListener,
                        // set DatePickerDialog to point to today's date when it loads up
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show()
                }

            })
            val mBulider = AlertDialog.Builder(context)
            mBulider.setView(dialogView).setTitle(R.string.title_add_customer)
            mBulider.setPositiveButton(R.string.update, { dialog, id->
              //  try {
                    val sale = SaleEntry(cal.time ,profile.cid, null,dialogView.noOfLoadET.text.toString().toInt(),dialogView.noOfEmptyET.text.toString().toInt(),dialogView.CollectedAmtET.text.toString().toDouble())
                    val bals = dbHelper.addSalesEntry(sale,profile)
                    profile.balAmt=bals[0].toString().toDouble()
                    profile.balEmpty=bals[1].toString().toInt()
                        updateBalance(item,profile.balAmt,profile.balEmpty)

                    Snackbar.make(view, "Added Successfully", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()

                //}catch()
            })
            mBulider.setNegativeButton(R.string.cancel_btn, { dialog, id->
                dialog.dismiss()
            })
            mBulider.show()
        }
    }

    class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val viewBtn:Button = view.viewBTN
        val editBtn: Button = view.editBTN
        val customerNameTV:TextView=view.customerNameTV
        val balanceTV:TextView=view.balanceTV
        val empty:TextView=view.emptyTV

    }

    fun updateBalance(item:ViewHolder,amt:Double,empty:Int){

        item.balanceTV.text = "₹"+ amt.toString()
        item.empty.text = empty.toString()

    }
}