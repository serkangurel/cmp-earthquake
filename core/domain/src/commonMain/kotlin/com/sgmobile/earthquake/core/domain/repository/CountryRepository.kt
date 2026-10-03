package com.sgmobile.earthquake.core.domain.repository

import com.sgmobile.earthquake.core.domain.models.Country

interface CountryRepository {

    suspend fun getCountries(): List<Country>
}
