package com.sgmobile.earthquake

import android.app.Application
import com.sgmobile.earthquake.feature.earthquake.navigation.earthquakeNavigationModule
import com.sgmobile.earthquake.feature.map.navigation.mapNavigationModule
import com.sgmobile.earthquake.feature.settings.navigation.settingsNavigationModule
import org.koin.core.context.loadKoinModules

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        val countryResource = assets.open("country-list.json")
            .bufferedReader()
            .use { it.readText() }
        startSharedApplication(countryResource, preferencesDirectory = filesDir.absolutePath)
        loadKoinModules(
            listOf(
                earthquakeNavigationModule,
                mapNavigationModule,
                settingsNavigationModule,
            )
        )
    }
}
