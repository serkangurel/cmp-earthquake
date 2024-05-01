package com.sgmobile.template

import android.app.Application
import appInit
import org.koin.android.ext.koin.androidContext

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        appInit {
            androidContext(this@Application)
        }
    }
}