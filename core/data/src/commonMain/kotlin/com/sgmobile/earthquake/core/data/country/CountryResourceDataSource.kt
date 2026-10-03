package com.sgmobile.earthquake.core.data.country

import kotlinx.serialization.json.Json
import org.koin.core.annotation.Property
import org.koin.core.annotation.Single

@Single
internal class CountryResourceDataSource(
    @Property("earthquake.countryResource")
    private val countryResource: String,
) {

    suspend fun getCountries(): List<CountryResource> {
        return decodeCountryResources(countryResource)
    }
}

internal fun decodeCountryResources(resource: String): List<CountryResource> {
    return Json.decodeFromString<CountriesResource>(resource).countries
}
