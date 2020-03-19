package com.karthikeyan.myapplication.adapter


import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import com.karthikeyan.myapplication.R
import com.karthikeyan.myapplication.dataInterface.CustomerProfile
import com.karthikeyan.myapplication.dataInterface.DataBaseHelper
import com.karthikeyan.myapplication.dataInterface.SaleEntry
import kotlinx.android.synthetic.main.dialog_update_sale.view.*
import kotlinx.android.synthetic.main.dialog_update_sale.view.dateOfEntryTV
import kotlinx.android.synthetic.main.item_customer_profile.view.*
import kotlinx.android.synthetic.main.item_customer_sale.view.*
import kotlinx.android.synthetic.main.item_day_sales.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

open class DaySalesListAdapter(val customerSalesList:ArrayList<SaleEntry>, val context: Context): RecyclerView.Adapter<DaySalesListAdapter.ViewHolder>(){
    lateinit var dbHelper: DataBaseHelper
    val myFormat = "dd/MM/yyyy" // mention the format you need
    fun dateConversion(date:Date) :String{

        val sdf = SimpleDateFormat(myFormat, Locale.US)
        return sdf.format(date.getTime())
    }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        dbHelper= DataBaseHelper(context)
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_day_sales,p0,false))
    }

    override fun getItemCount(): Int {
        return customerSalesList.size
    }
    fun addDaySalesList(cusSalesList:List<SaleEntry> ){
        customerSalesList.clear()
        customerSalesList.addAll(cusSalesList)
        notifyDataSetChanged()
    }
    fun addSales(cusSale: SaleEntry, pos:Int){

        customerSalesList.add(cusSale)
        notifyItemChanged(pos)
    }

    fun getList():ArrayList<SaleEntry>{
        return customerSalesList
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(item: ViewHolder, pos: Int) {
        val sale = customerSalesList.get(pos)
        item.name.text = sale.scname
        item.load.text = sale.load.toString()
        item.empty.text = sale.empty.toString()
        item.amt.text = "₹"+sale.amt.toString()

    }
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val name: TextView = view.sdNameTV
        val load: TextView = view.sdNoOfLoadTV
        val empty: TextView = view.sdNoOfEmptyTV
        val amt: TextView = view.sdCollectedAmtTV


    }
}