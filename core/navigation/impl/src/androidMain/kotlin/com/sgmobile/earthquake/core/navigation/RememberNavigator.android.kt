package com.sgmobile.earthquake.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun rememberNavigator(create: () -> Navigator): Navigator {
    val saver = Saver<Navigator, String>(
        save = { it.saveState() },
        restore = { saved -> create().also { it.restoreState(saved) } },
    )
    val navigator = rememberSaveable(saver = saver) { create() }
    DisposableEffect(navigator) {
        onDispose { navigator.close() }
    }
    return navigator
}
