package com.sgmobile.earthquake.feature.earthquake.overview.presentation

import com.sgmobile.earthquake.feature.earthquake.overview.domain.GetCountriesUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.Country
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.extensions.toOption
import org.koin.core.annotation.Single

@Single(binds = [CountryCatalog::class])
internal class CountryCatalogImpl(
    private val getCountriesUseCase: GetCountriesUseCase,
) : CountryCatalog {
    override suspend fun countries(): List<CountryOption> =
        getCountriesUseCase().map(Country::toOption)
}
