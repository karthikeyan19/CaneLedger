package com.karthikeyan.myapplication.utils


import com.google.gson.Gson
import com.karthikeyan.myapplication.CaneLedgerApp

class Utils{

    companion object{


        fun fromJsonToString( fileName:String):String{

            return CaneLedgerApp.appContext.assets.open(fileName).bufferedReader().use { it.readText() }

        }


    }
}