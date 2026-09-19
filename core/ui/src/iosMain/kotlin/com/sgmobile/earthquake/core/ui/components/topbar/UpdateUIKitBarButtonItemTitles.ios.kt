package com.sgmobile.earthquake.core.ui.components.topbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.uikit.LocalUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIBarButtonItem
import platform.UIKit.UINavigationBar

@OptIn(ExperimentalForeignApi::class)
@Composable
internal actual fun UpdateUIKitBarButtonItemTitles(titles: List<String?>) {
    val viewController = LocalUIViewController.current

    LaunchedEffect(viewController, titles) {
        // AdaptiveTopBar adds its UINavigationBar during the same composition.
        withFrameNanos { }

        val navigationBar = viewController.view.subviews
            .filterIsInstance<UINavigationBar>()
            .lastOrNull()
            ?: return@LaunchedEffect

        titles.forEachIndexed { index, title ->
            // Mutating the existing item preserves its UIMenu and avoids an empty transition frame.
            navigationBar.topItem?.rightBarButtonItems
                ?.filterIsInstance<UIBarButtonItem>()
                ?.getOrNull(index)
                ?.title = title
        }
    }
}
