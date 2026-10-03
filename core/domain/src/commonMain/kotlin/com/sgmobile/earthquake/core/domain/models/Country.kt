package com.sgmobile.earthquake.core.domain.models

data class Country(
    val code: String,
    val name: String,
    val flagUrl: String,
    val bounds: CountryBounds
)

data class CountryBounds(
    val minLongitude: Double,
    val minLatitude: Double,
    val maxLongitude: Double,
    val maxLatitude: Double
)
