package com.sgmobile.earthquake.core.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal actual fun AnimatedBottomBar(
    isVisible: Boolean,
    modifier: Modifier,
    animationDuration: Int,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(animationDuration, easing = FastOutSlowInEasing),
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(animationDuration, easing = FastOutSlowInEasing),
        ),
        modifier = modifier,
    ) {
        content()
    }
}
