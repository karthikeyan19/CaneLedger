package com.karthikeyan.myapplication

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import com.karthikeyan.myapplication.dataInterface.DataBaseHelper
import com.karthikeyan.myapplication.ui.main.*
import kotlinx.android.synthetic.main.activity_income_expanse.*
import kotlinx.android.synthetic.main.activity_income_expanse.view.*
import kotlinx.android.synthetic.main.dialog_day.view.*
import kotlinx.android.synthetic.main.dialog_get_dates.view.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.math.exp

class IncomeExpanseActivity : AppCompatActivity() {
    lateinit var dbHelper: DataBaseHelper
    lateinit var cal:Calendar
    var isExpand: Boolean = true
    lateinit var updateSales : UpdateSales
    lateinit var updateExpense:UpdateExpenseDetails
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income_expanse)
        cal = Calendar.getInstance()


        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        val income = sectionsPagerAdapter.getItem(0) as IncomeFragment
        val expense = sectionsPagerAdapter.getItem(1) as ExpenseFragment
        dbHelper= DataBaseHelper(this)

        showDayButton.text = "Today"
        showDayButton.setOnClickListener{

            if (isExpand) {
                showDaySelectionRG.visibility = View.VISIBLE
                showDayButton.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    this.resources.getDrawable(R.drawable.ic_baseline_keyboard_arrow_down_white_48dp, null),
                    null
                )

            } else {
                showDaySelectionRG.visibility = View.GONE
                showDayButton.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    this.getDrawable(R.drawable.ic_baseline_keyboard_arrow_right_white_48dp),
                    null
                )

            }
            isExpand = !isExpand
        }

         showDaySelectionRG.check(R.id.todayRB)
        showDaySelectionRG.setOnCheckedChangeListener{ rg, sid ->
            when (sid) {
                R.id.todayRB -> {
                    val todayRange = getTodayTimeRange()

                    income.updateDaySalesDetails(todayRange[0], todayRange[1])
                    expense.updateExpenseDetails(todayRange[0],todayRange[1])
                    showDayButton.text = "Today"
                    //adapter.addDaySalesList(daySales)
                }
                R.id.yesterdayRB -> {
                    val yesterdayRange = getYesterdayTimeRange()
                    income.updateDaySalesDetails(yesterdayRange[0],yesterdayRange[1])
                    expense.updateExpenseDetails(yesterdayRange[0],yesterdayRange[1])
                    showDayButton.text = "Yesterday"
                }
                R.id.cusDayRB -> {

                    val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_day, null)
                    val cal: Calendar = Calendar.getInstance()

                    val dateSetListener = object : DatePickerDialog.OnDateSetListener {
                        override fun onDateSet(
                            view: DatePicker, year: Int, monthOfYear: Int,
                            dayOfMonth: Int
                        ) {

                            cal.set(Calendar.YEAR, year)
                            cal.set(Calendar.MONTH, monthOfYear)
                            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            updateDateInView(dialogView.selectDateTIET, cal)
                        }
                    }
                    dialogView.selectDateTIET!!.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(view: View) {
                            DatePickerDialog(
                                this@IncomeExpanseActivity,
                                dateSetListener,
                                // set DatePickerDialog to point to today's date when it loads up
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }

                    })


                    val mBulider = AlertDialog.Builder(this)

                    mBulider.setView(dialogView).setTitle("Custom Date")
                    mBulider.setPositiveButton("Fetch", { dialog, id ->
                        Log.d("ss", "clicked")
                        val timeRange = getTimeRange(dialogView.selectDateTIET.text.toString())
                       // adapter.addDaySalesList(dbHelper.getDaySaleDetails(timeRange[0], timeRange[1]))
                        income.updateDaySalesDetails(timeRange[0],timeRange[1])
                        expense.updateExpenseDetails(timeRange[0],timeRange[1])
                        showDayButton.text = dialogView.selectDateTIET.text.toString()

                    }
                    )

                    mBulider.setNegativeButton(R.string.cancel_btn, { dialog, id ->
                        dialog.dismiss()

                    })
                    mBulider.show()


                }

            }
        }




    }

    @RequiresApi(Build.VERSION_CODES.O)
    open fun getTodayTimeRange():Array<Long>{

        val today = LocalDate.now()
        val sd = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        return arrayOf(sd.parse(today.dayOfMonth.toString()+"/"+today.monthValue+"/"+today.year+" 00:00:00").time,sd.parse(today.dayOfMonth.toString()+"/"+today.monthValue+"/"+today.year+" 23:59:59").time)
    }

    open fun getYesterdayTimeRange():Array<Long>{
        var timeRanges:Array<Long> = arrayOf(0,0)
        val sd = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val td = Calendar.getInstance()
        td.set(Calendar.HOUR_OF_DAY,0)
        td.set(Calendar.MINUTE,0)
        td.set(Calendar.SECOND,0)
        td.set(Calendar.MILLISECOND,0)
        td.add(Calendar.DATE,-1)
        timeRanges.set(0,td.time.time)
        Log.d("ss","ftr:"+sd.format(td.time))
        td.set(Calendar.HOUR_OF_DAY,23)
        td.set(Calendar.MINUTE,59)
        td.set(Calendar.SECOND,59)
        td.set(Calendar.MILLISECOND,59)
        timeRanges.set(1,td.time.time)
        Log.d("ss","ftr:"+sd.format(+timeRanges[1]))
        return timeRanges
    }


    open fun getTimeRange(strDate:String):Array<Long>{

        val sd = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        return arrayOf(sd.parse(strDate+" 00:00:00").time,sd.parse(strDate+" 23:59:59").time)

    }

    fun updateDateInView(dateEntryET: EditText, cal: Calendar) {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        dateEntryET!!.setText(sdf.format(cal.getTime()))
    }

}