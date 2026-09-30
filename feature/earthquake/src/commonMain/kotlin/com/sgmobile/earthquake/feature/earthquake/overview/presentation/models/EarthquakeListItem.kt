package com.sgmobile.earthquake.feature.earthquake.overview.presentation.models

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold

data class EarthquakeListItem(
    val id: String,
    val place: String,
    val magnitude: String,
    val magnitudeThreshold: MagnitudeThreshold,
    val date: String
)
