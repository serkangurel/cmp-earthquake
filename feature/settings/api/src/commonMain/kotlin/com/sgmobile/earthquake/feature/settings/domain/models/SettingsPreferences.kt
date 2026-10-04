package com.sgmobile.earthquake.feature.settings.domain.models

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.EarthquakeTimeRange
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.CountryOption

data class SettingsPreferences(
    /** Null until the user picks a theme, so the app follows the device appearance. */
    val theme: AppTheme?,
    val defaultMagnitude: MagnitudeThreshold,
    val defaultCountryCode: String,
    val timeRange: EarthquakeTimeRange,
) {
    fun effectiveTheme(isSystemInDarkTheme: Boolean): AppTheme =
        theme ?: if (isSystemInDarkTheme) AppTheme.DARK else AppTheme.LIGHT

    companion object {
        val DEFAULT = SettingsPreferences(
            theme = null,
            defaultMagnitude = MagnitudeThreshold.TWO_PLUS,
            defaultCountryCode = CountryOption.GLOBAL_CODE,
            timeRange = EarthquakeTimeRange.DEFAULT,
        )
    }
}
