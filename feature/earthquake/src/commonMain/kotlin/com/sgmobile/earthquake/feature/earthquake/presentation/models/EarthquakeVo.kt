package com.sgmobile.earthquake.feature.earthquake.presentation.models

import com.sgmobile.earthquake.feature.earthquake.domain.models.MagnitudeThreshold

internal data class EarthquakeVo(
    val place: String,
    val magnitude: String,
    val magnitudeThreshold: MagnitudeThreshold,
    val date: String
)