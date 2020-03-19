package com.karthikeyan.myapplication

import android.annotation.TargetApi
import android.app.Application
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import com.karthikeyan.myapplication.dataInterface.DataBaseHelper

open class  CaneLedgerApp : Application() {

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext;

    }
    companion object{
        lateinit var appContext:Context


    }





}