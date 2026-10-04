package com.sgmobile.earthquake.core.navigation

import kotlinx.coroutines.flow.StateFlow
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

interface Navigator {
    @OptIn(ExperimentalObjCRefinement::class)
    @HiddenFromObjC
    val state: StateFlow<NavigationSnapshot>
    val currentState: NavigationSnapshot

    fun navigate(destination: NavigationDestination)
    fun goBack()
    fun setBackStack(rootKey: String, destinationKeys: List<String>)
    fun resolveDestination(key: String): NavigationDestination?
    fun observe(observer: (NavigationSnapshot) -> Unit): NavigationObservation
    fun saveState(): String
    fun restoreState(savedState: String): Boolean
    fun close()
}

interface NavigationObservation {
    fun cancel()
}
