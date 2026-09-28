package com.sgmobile.earthquake.core.ui.util

import android.os.Build
import android.view.View
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider

@Composable
actual fun DisableNavigationBarContrastEnforcement() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return

    val view = LocalView.current
    DisposableEffect(view) {
        // ModalBottomSheet has its own window and does not inherit the activity's edge-to-edge setup.
        val dialogWindow = view.findDialogWindow()
        val wasContrastEnforced = dialogWindow?.isNavigationBarContrastEnforced
        dialogWindow?.isNavigationBarContrastEnforced = false

        onDispose {
            if (wasContrastEnforced != null) {
                dialogWindow.isNavigationBarContrastEnforced = wasContrastEnforced
            }
        }
    }
}

private fun View.findDialogWindow(): Window? {
    var currentView: View? = this
    while (currentView != null) {
        if (currentView is DialogWindowProvider) return currentView.window
        currentView = currentView.parent as? View
    }
    return null
}
