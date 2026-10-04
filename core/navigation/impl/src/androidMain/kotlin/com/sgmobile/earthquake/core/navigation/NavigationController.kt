package com.sgmobile.earthquake.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.compositionLocalOf

val LocalNavigator = compositionLocalOf<Navigator> {
    error("No Navigator provided")
}

val LocalNavScaffoldPadding = compositionLocalOf<PaddingValues> {
    error("No NavScaffoldPadding provided.")
}
