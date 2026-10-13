package com.sgmobile.earthquake.feature.earthquake.overview.domain

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.CountryBounds
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.EarthquakeSource
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import org.koin.core.annotation.Factory

@Factory
internal class RefreshEarthquakesUseCase(
    private val earthquakeRepository: EarthquakeRepository,
    private val getEarthquakeStartTimeUseCase: GetEarthquakeStartTimeUseCase
) {
    suspend operator fun invoke(
        source: EarthquakeSource,
        pageSize: Int,
        selectedMagnitude: MagnitudeThreshold,
        selectedCountryBounds: CountryBounds,
    ) = earthquakeRepository.refresh(
        source = source,
        startTime = getEarthquakeStartTimeUseCase(),
        pageSize = pageSize,
        selectedMagnitude = selectedMagnitude,
        selectedCountryBounds = selectedCountryBounds,
    )
}
