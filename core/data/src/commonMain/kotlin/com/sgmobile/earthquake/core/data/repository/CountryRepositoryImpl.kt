package com.sgmobile.earthquake.core.data.repository

import com.sgmobile.earthquake.core.data.country.CountryResourceDataSource
import com.sgmobile.earthquake.core.data.extensions.toDomain
import com.sgmobile.earthquake.core.domain.models.Country
import com.sgmobile.earthquake.core.domain.repository.CountryRepository
import org.koin.core.annotation.Single

@Single(binds = [CountryRepository::class])
internal class CountryRepositoryImpl(
    private val countryResourceDataSource: CountryResourceDataSource
) : CountryRepository {

    override suspend fun getCountries(): List<Country> {
        return countryResourceDataSource.getCountries().map { country ->
            country.toDomain()
        }
    }
}
