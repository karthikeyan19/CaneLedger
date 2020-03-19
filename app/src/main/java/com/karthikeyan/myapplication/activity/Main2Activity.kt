package com.karthikeyan.myapplication.activity

import android.content.DialogInterface
import android.opengl.Visibility
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater
import android.view.View
import com.karthikeyan.myapplication.R

import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.activity_main2.fab
import kotlinx.android.synthetic.main.activity_scrolling.*

class Main2Activity : AppCompatActivity() {
    var isExpand=true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(toolbar)
//        showButton.text="Jan - 20"
//        showButton.setOnClickListener{view ->
//            if(isExpand){
//               showSelectionRG.visibility= View.VISIBLE
//                showButton.setCompoundDrawablesWithIntrinsicBounds(null,null,this.resources.getDrawable(R.drawable.ic_baseline_keyboard_arrow_down_black_36dp,null),null)
//
//            }else{
//                showSelectionRG.visibility=View.GONE
//                showButton.setCompoundDrawablesWithIntrinsicBounds(null,null,this.getDrawable(R.drawable.ic_baseline_keyboard_arrow_right_black_36dp),null)
//
//            }
//            isExpand=!isExpand
//        }
        fab.setOnClickListener { view ->
           // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
           //     .setAction("Action", null).show()






        }
    }

}
