package com.karthikeyan.myapplication.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import com.karthikeyan.myapplication.CaneLedgerApp
import com.karthikeyan.myapplication.R
import com.karthikeyan.myapplication.adapter.CustomerListAdapter
import com.karthikeyan.myapplication.dataInterface.CustomerProfile
import com.karthikeyan.myapplication.dataInterface.DataBaseHelper

import kotlinx.android.synthetic.main.activity_cust_list.*
import kotlinx.android.synthetic.main.content_cust_list.*
import kotlinx.android.synthetic.main.dialog_add_customer.view.*

class CustListActivity : AppCompatActivity() {
lateinit var dbHelper:DataBaseHelper

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cust_list)
        setSupportActionBar(toolbar)
        customerListRV.layoutManager =  LinearLayoutManager(this)
        dbHelper=DataBaseHelper(this)
        val custAdapter = CustomerListAdapter(dbHelper.getAllProfile() as ArrayList<CustomerProfile>,this)
        customerListRV.adapter = custAdapter
        fab.setOnClickListener { view ->

            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_customer,null)
            val mBulider = AlertDialog.Builder(this)
            mBulider.setView(dialogView).setTitle(R.string.title_add_customer)
            mBulider.setPositiveButton(R.string.add_btn, { dialog, id->
                val cus = CustomerProfile(0,dialogView.custmerNameED.text.toString(),dialogView.canePriceED.text.toString().toDouble(),0,0.0)
              dbHelper.addCustomer(cus)
              custAdapter.addCustomer(cus,custAdapter.itemCount)
                Snackbar.make(view, "New Customer Added", Snackbar.LENGTH_LONG)
                     .setAction("Action", null).show()
            })
            mBulider.setNegativeButton(R.string.cancel_btn, { dialog, id->
                dialog.dismiss()
            })
            mBulider.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_cus_list,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)
        when(item!!.itemId){
            R.id.action_daySales->{

                val i = Intent(this,DaySaleInfoActivity::class.java)
                startActivity(i)


            }
        }
        return true
    }

}
