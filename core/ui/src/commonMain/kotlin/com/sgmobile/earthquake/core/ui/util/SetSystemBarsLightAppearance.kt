package com.sgmobile.earthquake.core.ui.util

import androidx.compose.runtime.Composable

@Composable
expect fun SetSystemBarsLightAppearance(
    isAppearanceLightStatusBars: Boolean,
    isAppearanceLightNavigationBars: Boolean
)