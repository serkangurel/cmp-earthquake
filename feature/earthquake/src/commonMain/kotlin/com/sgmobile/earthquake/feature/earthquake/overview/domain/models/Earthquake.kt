package com.sgmobile.earthquake.feature.earthquake.overview.domain.models

internal data class Earthquake(
    val id: String,
    val place: String,
    val magnitude: String,
    val magnitudeThreshold: MagnitudeThreshold,
    val depth: String,
    val date: String,
    val latitude: Double,
    val longitude: Double,
)