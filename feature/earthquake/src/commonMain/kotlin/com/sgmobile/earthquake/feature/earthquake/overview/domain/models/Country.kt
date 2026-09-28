package com.sgmobile.earthquake.feature.earthquake.overview.domain.models

internal data class Country(
    val code: String,
    val name: String,
    val flagUrl: String,
    val bounds: CountryBounds
)

internal data class CountryBounds(
    val minLongitude: Double,
    val minLatitude: Double,
    val maxLongitude: Double,
    val maxLatitude: Double
)
