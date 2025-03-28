package com.sgmobile.earthquake

import android.app.Application
import org.koin.android.ext.koin.androidContext

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        appInit {
            androidContext(this@Application)
        }
    }
}