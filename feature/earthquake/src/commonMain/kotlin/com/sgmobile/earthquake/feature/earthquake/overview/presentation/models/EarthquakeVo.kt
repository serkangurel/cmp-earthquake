package com.sgmobile.earthquake.feature.earthquake.overview.presentation.models

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold

internal data class EarthquakeVo(
    val id: String,
    val place: String,
    val magnitude: String,
    val magnitudeThreshold: MagnitudeThreshold,
    val date: String
)