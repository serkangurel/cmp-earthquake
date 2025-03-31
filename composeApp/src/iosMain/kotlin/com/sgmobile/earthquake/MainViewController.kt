package com.sgmobile.earthquake

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController(
    configure = { appInit() }
) {
    App(
        darkTheme = isSystemInDarkTheme(),
        dynamicColor = false
    )
}