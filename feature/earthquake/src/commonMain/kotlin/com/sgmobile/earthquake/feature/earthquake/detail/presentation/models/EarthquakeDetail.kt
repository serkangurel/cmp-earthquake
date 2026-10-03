package com.sgmobile.earthquake.feature.earthquake.detail.presentation.models

import com.sgmobile.earthquake.core.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.core.presentation.models.EarthquakeTimestamp

private const val EARTHQUAKE_DATA_SOURCE = "USGS"

data class EarthquakeDetail(
    val place: String,
    val magnitude: String,
    val magnitudeThreshold: MagnitudeThreshold,
    val depth: String,
    val date: String,
    val latitude: Double,
    val longitude: Double,
) {
    val timestamp: EarthquakeTimestamp = EarthquakeTimestamp.fromDisplayValue(date)

    /** Depth in kilometers, or null when the feed did not report one. */
    val depthKm: String?
        get() = depth.takeUnless { it.isBlank() }

    val source: String
        get() = EARTHQUAKE_DATA_SOURCE
}
