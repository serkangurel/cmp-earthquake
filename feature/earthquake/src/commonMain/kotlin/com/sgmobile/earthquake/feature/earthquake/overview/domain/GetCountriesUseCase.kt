package com.sgmobile.earthquake.feature.earthquake.overview.domain

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.Country
import org.koin.core.annotation.Factory

@Factory
internal class GetCountriesUseCase(
    private val countryRepository: CountryRepository
) {
    suspend operator fun invoke(): List<Country> {
        return countryRepository.getCountries()
    }
}
