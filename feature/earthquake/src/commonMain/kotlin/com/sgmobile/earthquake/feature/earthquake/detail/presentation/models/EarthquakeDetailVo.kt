package com.sgmobile.earthquake.feature.earthquake.detail.presentation.models

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold

internal data class EarthquakeDetailVo(
    val place: String,
    val magnitude: String,
    val magnitudeThreshold: MagnitudeThreshold,
    val depth: String,
    val date: String,
    val latitude: Double,
    val longitude: Double,
)
