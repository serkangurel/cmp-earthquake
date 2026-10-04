package com.sgmobile.earthquake.feature.settings.presentation.extensions

import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.about_usgs_copyrights_and_credits
import com.sgmobile.earthquake.core.resource.about_usgs_earthquake_hazards
import com.sgmobile.earthquake.core.resource.about_usgs_privacy_policy
import com.sgmobile.earthquake.core.resource.theme_dark
import com.sgmobile.earthquake.core.resource.theme_light
import com.sgmobile.earthquake.core.resource.time_range_last_day
import com.sgmobile.earthquake.core.resource.time_range_last_month
import com.sgmobile.earthquake.core.resource.time_range_last_week
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.EarthquakeTimeRange
import com.sgmobile.earthquake.feature.settings.domain.models.AppTheme
import com.sgmobile.earthquake.feature.settings.presentation.AboutLink
import org.jetbrains.compose.resources.StringResource

internal val AppTheme.label: StringResource
    get() = when (this) {
        AppTheme.LIGHT -> Res.string.theme_light
        AppTheme.DARK -> Res.string.theme_dark
    }

internal val EarthquakeTimeRange.label: StringResource
    get() = when (this) {
        EarthquakeTimeRange.LAST_DAY -> Res.string.time_range_last_day
        EarthquakeTimeRange.LAST_WEEK -> Res.string.time_range_last_week
        EarthquakeTimeRange.LAST_MONTH -> Res.string.time_range_last_month
    }

internal val AboutLink.title: StringResource
    get() = when (this) {
        AboutLink.USGS_EARTHQUAKE_HAZARDS -> Res.string.about_usgs_earthquake_hazards
        AboutLink.USGS_COPYRIGHTS_AND_CREDITS -> Res.string.about_usgs_copyrights_and_credits
        AboutLink.USGS_PRIVACY_POLICY -> Res.string.about_usgs_privacy_policy
    }
