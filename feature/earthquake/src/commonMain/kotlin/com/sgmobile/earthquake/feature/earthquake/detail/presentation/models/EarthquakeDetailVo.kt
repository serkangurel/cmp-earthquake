package com.sgmobile.earthquake.feature.earthquake.detail.presentation.models

internal data class EarthquakeDetailVo(
    val place: String,
    val magnitude: String,
    val depth: String,
    val date: String,
    val latitude: String,
    val longitude: String,
)
