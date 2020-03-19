package com.karthikeyan.myapplication.ui.main

import android.annotation.TargetApi
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import com.karthikeyan.myapplication.IncomeExpanseActivity
import com.karthikeyan.myapplication.R
import com.karthikeyan.myapplication.adapter.DaySalesListAdapter
import com.karthikeyan.myapplication.dataInterface.SaleEntry
import kotlinx.android.synthetic.main.activity_income_expanse.*
import kotlinx.android.synthetic.main.activity_income_expanse.view.*
import kotlinx.android.synthetic.main.content_day_sale_info.view.*
import kotlinx.android.synthetic.main.dialog_day.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
interface UpdateSales{
    fun updateDaySalesDetails(fromTime: Long, toTime:Long)
}
open class IncomeFragment : Fragment(),UpdateSales  {

    var daySales: ArrayList<SaleEntry> = ArrayList()
    lateinit var adapter: DaySalesListAdapter;
    var isExpand: Boolean = true


    lateinit var incomeActivity: IncomeExpanseActivity;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        incomeActivity = activity as IncomeExpanseActivity
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        return inflater.inflate(R.layout.content_day_sale_info, container, false)
    }

    @TargetApi(Build.VERSION_CODES.P)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val todayRange = incomeActivity.getTodayTimeRange()
        daySales = incomeActivity.dbHelper.getDaySaleDetails(todayRange[0], todayRange[1])


        view.daySalesRV.layoutManager = LinearLayoutManager(context)
        adapter = context?.let { DaySalesListAdapter(daySales, it) }!!
        view.daySalesRV.adapter = adapter


    }

    fun updateDateInView(dateEntryET: EditText, cal: Calendar) {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        dateEntryET!!.setText(sdf.format(cal.getTime()))
    }

    @RequiresApi(Build.VERSION_CODES.P)
     override fun updateDaySalesDetails(fromTime: Long, toTime: Long) {
        Log.d("ss","Interface called")
        daySales =incomeActivity.dbHelper.getDaySaleDetails(fromTime, toTime)
        adapter.addDaySalesList(daySales)
    }


}