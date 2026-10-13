package com.sgmobile.earthquake.feature.earthquake.detail.presentation.models

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.models.EarthquakeTimestamp

data class EarthquakeDetail(
    val place: String,
    val magnitude: String,
    val magnitudeThreshold: MagnitudeThreshold,
    val depth: String,
    val date: String,
    val latitude: Double,
    val longitude: Double,
    val source: String,
) {
    val timestamp: EarthquakeTimestamp = EarthquakeTimestamp.fromDisplayValue(date)

    /** Depth in kilometers, or null when the feed did not report one. */
    val depthKm: String?
        get() = depth.takeUnless { it.isBlank() }
}
