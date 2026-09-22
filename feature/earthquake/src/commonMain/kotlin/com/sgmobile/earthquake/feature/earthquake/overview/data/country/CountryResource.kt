package com.sgmobile.earthquake.feature.earthquake.overview.data.country

import kotlinx.serialization.Serializable

@Serializable
internal data class CountriesResource(
    val countries: List<CountryResource>
)

@Serializable
internal data class CountryResource(
    val code: String,
    val name: String,
    val flagUrl: String,
    val bounds: CountryBoundsResource
)

@Serializable
internal data class CountryBoundsResource(
    val minLongitude: Double,
    val minLatitude: Double,
    val maxLongitude: Double,
    val maxLatitude: Double
)
