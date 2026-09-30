package com.sgmobile.earthquake

import androidx.compose.runtime.Composable
import com.sgmobile.earthquake.core.navigation.SGNavHost
import com.sgmobile.earthquake.core.ui.theme.AppTheme
import com.sgmobile.earthquake.core.ui.util.SetSystemBarsLightAppearance

@Composable
fun App(
    darkTheme: Boolean,
    dynamicColor: Boolean
) {
    AppTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
    ) {
        SetSystemBarsLightAppearance(
            isAppearanceLightStatusBars = darkTheme,
            isAppearanceLightNavigationBars = !darkTheme,
        )
        SGNavHost()
    }
}
