package com.sgmobile.earthquake.core.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation3.scene.Scene

internal fun <T : Any> AnimatedContentTransitionScope<Scene<T>>.navigationTransition(
    topLevelContentKeys: Set<Any>,
    layoutDirection: LayoutDirection,
    isPop: Boolean,
): ContentTransform {
    val initialTab = initialState.topLevelContentKey(topLevelContentKeys)
    val targetTab = targetState.topLevelContentKey(topLevelContentKeys)
    if (initialTab != null && targetTab != null && initialTab != targetTab) {
        // Only fade the upper scene; fading both exposes the background between opaque screens.
        return if (isPop) {
            EnterTransition.None togetherWith fadeOut(tween(180))
        } else {
            fadeIn(tween(180)) togetherWith ExitTransition.KeepUntilTransitionsFinished
        }
    }

    val direction = if (layoutDirection == LayoutDirection.Ltr) 1 else -1
    val enter = slideInHorizontally(
        animationSpec = tween(280, easing = FastOutSlowInEasing),
        initialOffsetX = { width ->
            if (isPop) -direction * width / 4 else direction * width
        },
    ) + fadeIn(tween(280))
    val exit = slideOutHorizontally(
        animationSpec = tween(280, easing = FastOutSlowInEasing),
        targetOffsetX = { width ->
            if (isPop) direction * width else -direction * width / 4
        },
    ) + fadeOut(tween(280))
    return enter togetherWith exit
}

private fun Scene<*>.topLevelContentKey(topLevelContentKeys: Set<Any>): Any? =
    // Include previous entries so a restored detail screen still belongs to its original tab.
    (previousEntries + entries).lastOrNull { it.contentKey in topLevelContentKeys }?.contentKey
