package com.sgmobile.earthquake.feature.map.presentation

import com.sgmobile.earthquake.feature.earthquake.presentation.Observation
import kotlinx.coroutines.flow.StateFlow
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

interface EarthquakeMapController {
    @OptIn(ExperimentalObjCRefinement::class)
    @HiddenFromObjC
    val state: StateFlow<EarthquakeMapState>
    val currentState: EarthquakeMapState

    fun selectEarthquake(id: String)
    fun dismissSelection()
    fun observe(observer: (EarthquakeMapState) -> Unit): Observation
    fun close()
}
