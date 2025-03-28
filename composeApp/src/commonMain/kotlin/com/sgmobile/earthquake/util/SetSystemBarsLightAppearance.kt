package com.sgmobile.earthquake.util

import androidx.compose.runtime.Composable

@Composable
expect fun SetSystemBarsLightAppearance(
    isAppearanceLightStatusBars: Boolean,
    isAppearanceLightNavigationBars: Boolean
)