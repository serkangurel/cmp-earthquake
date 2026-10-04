package com.sgmobile.earthquake.core.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun AnimatedBottomBar(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    animationDuration: Int = 220,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(animationDuration, easing = FastOutSlowInEasing),
        ) + fadeIn(animationSpec = tween(animationDuration)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(animationDuration, easing = FastOutSlowInEasing),
        ) + fadeOut(animationSpec = tween(animationDuration)),
        modifier = modifier,
    ) {
        content()
    }
}
