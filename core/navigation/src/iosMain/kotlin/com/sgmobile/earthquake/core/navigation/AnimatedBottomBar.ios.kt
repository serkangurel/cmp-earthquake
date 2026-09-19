package com.sgmobile.earthquake.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal actual fun AnimatedBottomBar(
    isVisible: Boolean,
    modifier: Modifier,
    animationDuration: Int,
    content: @Composable () -> Unit,
) {
    if (isVisible) {
        Box(modifier = modifier) {
            content()
        }
    }
}
