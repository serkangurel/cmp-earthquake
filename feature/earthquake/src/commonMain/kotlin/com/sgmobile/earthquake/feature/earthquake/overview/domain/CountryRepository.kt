package com.sgmobile.earthquake.feature.earthquake.overview.domain

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.Country

internal interface CountryRepository {

    suspend fun getCountries(): List<Country>
}
