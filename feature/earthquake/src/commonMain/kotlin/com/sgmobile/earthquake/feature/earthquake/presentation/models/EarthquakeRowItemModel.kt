package com.sgmobile.earthquake.feature.earthquake.presentation.models

internal data class EarthquakeRowItemModel(
    val place: String,
    val magnitude: String,
    val depth: String,
    val date: String
)