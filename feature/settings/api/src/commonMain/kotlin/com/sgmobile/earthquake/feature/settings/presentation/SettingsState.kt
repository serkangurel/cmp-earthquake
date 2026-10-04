package com.sgmobile.earthquake.feature.settings.presentation

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.EarthquakeTimeRange
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.CountryOption
import com.sgmobile.earthquake.feature.settings.domain.models.AppTheme
import com.sgmobile.earthquake.feature.settings.domain.models.SettingsPreferences

data class SettingsState(
    val preferences: SettingsPreferences,
    val countries: List<CountryOption>,
) {
    val defaultCountry: CountryOption?
        get() = countries.firstOrNull { it.code == preferences.defaultCountryCode }

    val themeOptions: List<AppTheme>
        get() = AppTheme.entries

    val magnitudeOptions: List<MagnitudeThreshold>
        get() = MagnitudeThreshold.entries

    val timeRangeOptions: List<EarthquakeTimeRange>
        get() = EarthquakeTimeRange.entries

    val aboutLinks: List<AboutLink>
        get() = AboutLink.entries

    companion object {
        val INITIAL = SettingsState(
            preferences = SettingsPreferences.DEFAULT,
            countries = emptyList(),
        )
    }
}
