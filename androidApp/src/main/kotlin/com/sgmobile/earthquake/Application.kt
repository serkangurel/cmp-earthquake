package com.sgmobile.earthquake

import android.app.Application

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        appInit()
    }
}