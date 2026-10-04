package com.sgmobile.earthquake.feature.settings.domain

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.EarthquakeTimeRange
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.settings.domain.models.AppTheme

internal interface SettingsRepository : SettingsSource {
    fun setTheme(theme: AppTheme)
    fun setDefaultMagnitude(magnitude: MagnitudeThreshold)
    fun setDefaultCountryCode(countryCode: String)
    fun setTimeRange(timeRange: EarthquakeTimeRange)
}
