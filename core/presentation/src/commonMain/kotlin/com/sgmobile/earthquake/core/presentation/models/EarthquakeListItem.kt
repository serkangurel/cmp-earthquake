package com.sgmobile.earthquake.core.presentation.models

import com.sgmobile.earthquake.core.domain.models.MagnitudeThreshold

data class EarthquakeListItem(
    val id: String,
    val place: String,
    val magnitude: String,
    val magnitudeThreshold: MagnitudeThreshold,
    val date: String
) {
    val timestamp: EarthquakeTimestamp = EarthquakeTimestamp.fromDisplayValue(date)
}
