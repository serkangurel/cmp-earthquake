package com.sgmobile.earthquake.feature.earthquake.detail.presentation

import com.sgmobile.earthquake.feature.earthquake.presentation.Observation
import kotlinx.coroutines.flow.StateFlow
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

interface EarthquakeDetailController {
    @OptIn(ExperimentalObjCRefinement::class)
    @HiddenFromObjC
    val state: StateFlow<EarthquakeDetailState>
    val currentState: EarthquakeDetailState

    fun observe(observer: (EarthquakeDetailState) -> Unit): Observation
    fun close()
}
