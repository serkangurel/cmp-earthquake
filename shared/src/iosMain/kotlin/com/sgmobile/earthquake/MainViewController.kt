package com.sgmobile.earthquake

import GoogleMaps.GMSServices
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
fun MainViewController() = ComposeUIViewController(
    configure = {
        appInit()
        GMSServices.provideAPIKey(BuildKonfig.MAPS_API_KEY)
    }
) {
    App(
        darkTheme = isSystemInDarkTheme(),
        dynamicColor = false
    )
}
