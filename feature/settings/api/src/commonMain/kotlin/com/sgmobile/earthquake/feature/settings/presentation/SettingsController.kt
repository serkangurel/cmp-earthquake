package com.sgmobile.earthquake.feature.settings.presentation

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.EarthquakeTimeRange
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.presentation.Observation
import com.sgmobile.earthquake.feature.settings.domain.models.AppTheme
import kotlinx.coroutines.flow.StateFlow
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

interface SettingsController {
    @OptIn(ExperimentalObjCRefinement::class)
    @HiddenFromObjC
    val state: StateFlow<SettingsState>
    val currentState: SettingsState

    fun observe(observer: (SettingsState) -> Unit): Observation
    fun onIntent(intent: SettingsIntent)
    fun selectTheme(theme: AppTheme)
    fun selectDefaultMagnitude(magnitude: MagnitudeThreshold)
    fun selectDefaultCountry(countryCode: String)
    fun selectTimeRange(timeRange: EarthquakeTimeRange)
    fun close()
}
