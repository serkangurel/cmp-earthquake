package com.sgmobile.earthquake.feature.earthquake.domain.models

internal data class Earthquake(
    val place: String,
    val magnitude: String,
    val magnitudeThreshold: MagnitudeThreshold,
    val depth: String,
    val date: String,
)