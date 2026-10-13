package com.sgmobile.earthquake.feature.earthquake.overview.data.extensions

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.EarthquakeDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

internal fun Instant.toEarthquakeDate(): String =
    EarthquakeDateTime.format(toLocalDateTime(TimeZone.currentSystemDefault()))
