package com.sgmobile.earthquake.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import com.sgmobile.earthquake.extensions.getActivity

@Composable
actual fun SetSystemBarsLightAppearance(
    isAppearanceLightStatusBars: Boolean,
    isAppearanceLightNavigationBars: Boolean
) {
    val context: Context = LocalContext.current
    LaunchedEffect(key1 = isAppearanceLightStatusBars, key2 = isAppearanceLightNavigationBars) {
        context.getActivity()?.let { activity ->
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