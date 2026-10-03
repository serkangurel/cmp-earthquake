package com.sgmobile.earthquake.core.domain.usecase

import com.sgmobile.earthquake.core.domain.models.CountryBounds
import com.sgmobile.earthquake.core.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.core.domain.repository.EarthquakeRepository
import org.koin.core.annotation.Factory

@Factory
class RefreshUsgsEarthquakesUseCase(
    private val earthquakeRepository: EarthquakeRepository,
    private val getUsgsEarthquakeStartTimeUseCase: GetUsgsEarthquakeStartTimeUseCase
) {
    suspend operator fun invoke(
        pageSize: Int,
        selectedMagnitude: MagnitudeThreshold,
        selectedCountryBounds: CountryBounds,
    ) = earthquakeRepository.refresh(
        startTime = getUsgsEarthquakeStartTimeUseCase(),
        pageSize = pageSize,
        selectedMagnitude = selectedMagnitude,
        selectedCountryBounds = selectedCountryBounds,
    )
}
