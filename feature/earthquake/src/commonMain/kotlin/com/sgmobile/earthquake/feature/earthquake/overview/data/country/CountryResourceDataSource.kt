package com.sgmobile.earthquake.feature.earthquake.overview.data.country

import com.sgmobile.earthquake.core.resource.Res
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

private const val COUNTRY_RESOURCE_PATH = "files/country-list.json"

@Single
internal class CountryResourceDataSource {

    suspend fun getCountries(): List<CountryResource> {
        val resource = Res.readBytes(COUNTRY_RESOURCE_PATH).decodeToString()
        return decodeCountryResources(resource)
    }
}

internal fun decodeCountryResources(resource: String): List<CountryResource> {
    return Json.decodeFromString<CountriesResource>(resource).countries
}
