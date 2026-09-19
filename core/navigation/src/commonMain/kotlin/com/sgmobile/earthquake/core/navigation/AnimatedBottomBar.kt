package com.sgmobile.earthquake.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal expect fun AnimatedBottomBar(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    animationDuration: Int = 300,
    content: @Composable () -> Unit,
)
