package com.sgmobile.earthquake.feature.earthquake.overview.data

import com.sgmobile.earthquake.feature.earthquake.overview.data.country.CountryResourceDataSource
import com.sgmobile.earthquake.feature.earthquake.overview.data.extensions.toDomain
import com.sgmobile.earthquake.feature.earthquake.overview.domain.CountryRepository
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.Country
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
