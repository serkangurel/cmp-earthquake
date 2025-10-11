package com.sgmobile.earthquake.core.ui.util

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat

@Composable
actual fun SetSystemBarsLightAppearance(
    isAppearanceLightStatusBars: Boolean,
    isAppearanceLightNavigationBars: Boolean
) {
    val activity = LocalActivity.current
    LaunchedEffect(key1 = isAppearanceLightStatusBars, key2 = isAppearanceLightNavigationBars) {
        activity?.let { activity ->
            WindowCompat.getInsetsController(
                activity.window,
                activity.window.decorView
            ).apply {
                this.isAppearanceLightStatusBars = isAppearanceLightStatusBars
                this.isAppearanceLightNavigationBars = isAppearanceLightNavigationBars
            }
        }
    }
}