package com.sgmobile.earthquake.core.domain.usecase

import com.sgmobile.earthquake.core.domain.models.Country
import com.sgmobile.earthquake.core.domain.repository.CountryRepository
import org.koin.core.annotation.Factory

@Factory
class GetCountriesUseCase(
    private val countryRepository: CountryRepository
) {
    suspend operator fun invoke(): List<Country> {
        return countryRepository.getCountries()
    }
}
