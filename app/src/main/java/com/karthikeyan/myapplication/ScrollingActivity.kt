package com.karthikeyan.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_scrolling.*

class ScrollingActivity : AppCompatActivity() {
   var isExpand=true
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        app_bar.setExpanded(false,false)
        setSupportActionBar(toolbar)
        buttonExpand.setOnClickListener{view ->
            if(isExpand){
                app_bar.setExpanded(true,false)

            }else{
                app_bar.setExpanded(false,false)

            }
            isExpand=!isExpand
        }
        fab.setOnClickListener { view ->

            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }
}
