package com.karthikeyan.myapplication.ui.main

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import com.karthikeyan.myapplication.IncomeExpanseActivity
import com.karthikeyan.myapplication.R
import com.karthikeyan.myapplication.adapter.ExpenseListAdapter
import com.karthikeyan.myapplication.dataInterface.ExpenseEntry
import kotlinx.android.synthetic.main.dialog_add_expense.view.*
import kotlinx.android.synthetic.main.dialog_update_sale.view.*
import kotlinx.android.synthetic.main.fragment_expense.view.*
import java.text.SimpleDateFormat
import java.time.Year
import java.util.*
import kotlin.collections.ArrayList

interface UpdateExpenseDetails{
    fun updateExpenseDetails(fromTime:Long,toTime:Long)

}

class ExpenseFragment :Fragment() ,UpdateExpenseDetails{


    lateinit var expenseList:ArrayList<ExpenseEntry>
    lateinit var expenseActivity:IncomeExpanseActivity;
    lateinit var cal:Calendar
    lateinit var adapter:ExpenseListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        expenseList = ArrayList()
        expenseActivity = activity as IncomeExpanseActivity
        cal= Calendar.getInstance()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_expense,container,false)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            adapter= context?.let { ExpenseListAdapter(expenseList, it) }!!
            view.expenseRV.layoutManager = context?.let { LinearLayoutManager(it) } as RecyclerView.LayoutManager?
            view.expenseRV.adapter = adapter
            val today = expenseActivity.getTodayTimeRange()
            adapter.addExpenseList(expenseActivity.dbHelper.getExpenseDetails(today[0],today[1]))
            view.addExpenseFAB.setOnClickListener { view2->
                val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_expense,null)
                updateDateInView(dialogView.exDateOfEntryET,cal)
                val mBulider = context?.let{AlertDialog.Builder(it)}

                val tdateSetListener = object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                           dayOfMonth: Int) {

                        cal.set(Calendar.YEAR, year)
                        cal.set(Calendar.MONTH, monthOfYear)
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        updateDateInView(dialogView.exDateOfEntryET,cal)

                    }
                }
                dialogView.exDateOfEntryET.setOnClickListener {view->
                    DatePickerDialog(context,tdateSetListener,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()



                }
                mBulider!!.setView(dialogView).setTitle("Add Expense")

                mBulider.setPositiveButton("Update",{dialog,id->
                    val expenseItem = ExpenseEntry(cal.time,dialogView.expenseLabelET.text.toString(),dialogView.expenseAmtET.text.toString().toDouble())
                    val new_id = expenseActivity.dbHelper.addExpenseEntry(expenseItem)
                    Log.d("ss",new_id.toString()+" inserted")
                    adapter.addExpense(expenseItem)
                })
                mBulider.setNegativeButton("cancel",{dialog2,id->
                    dialog2.dismiss()
                })
                mBulider.show()

             }
    }

    fun updateDateInView(dateEntryET: EditText, cal: Calendar) {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        dateEntryET.setText(sdf.format(cal.getTime()))
    }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun updateExpenseDetails(fromTime: Long, toTime: Long) {
     //   Log.d("ss","Expense Interface is called" + SimpleDateFormat("dd/MM/YYYY").format(fromTime))
        adapter.addExpenseList(expenseActivity.dbHelper.getExpenseDetails(fromTime,toTime))

    }
}