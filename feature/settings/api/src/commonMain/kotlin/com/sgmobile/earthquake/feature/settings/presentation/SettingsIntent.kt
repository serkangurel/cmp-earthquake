package com.sgmobile.earthquake.feature.settings.presentation

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.EarthquakeTimeRange
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.settings.domain.models.AppTheme

sealed interface SettingsIntent {
    data class SelectTheme(val theme: AppTheme) : SettingsIntent
    data class SelectDefaultMagnitude(val magnitude: MagnitudeThreshold) : SettingsIntent
    data class SelectDefaultCountry(val countryCode: String) : SettingsIntent
    data class SelectTimeRange(val timeRange: EarthquakeTimeRange) : SettingsIntent
}
