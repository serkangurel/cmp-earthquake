package com.sgmobile.earthquake.feature.earthquake.overview.presentation

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.presentation.Observation
import kotlinx.coroutines.flow.StateFlow
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

interface EarthquakeOverviewController {
    @OptIn(ExperimentalObjCRefinement::class)
    @HiddenFromObjC
    val state: StateFlow<EarthquakeOverviewState>
    val currentState: EarthquakeOverviewState

    fun observe(observer: (EarthquakeOverviewState) -> Unit): Observation
    fun refresh()
    fun refresh(completion: () -> Unit)
    fun onIntent(intent: EarthquakeScreenIntent)
    fun loadMore()
    fun onEarthquakeDisplayed(id: String)
    fun selectMagnitude(magnitude: MagnitudeThreshold)
    fun selectCountry(countryCode: String)
    fun close()
}
