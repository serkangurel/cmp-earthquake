package com.sgmobile.earthquake.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController

val LocalNavController = compositionLocalOf<NavHostController> {
    error("No NavController provided")
}

val LocalNavScaffoldPadding = compositionLocalOf<PaddingValues> {
    error("No NavScaffoldPadding provided.")
}
