package com.sgmobile.earthquake.feature.earthquake.overview.domain

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.CountryBounds
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.EarthquakeTimeRange
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import org.koin.core.annotation.Factory

@Factory
internal class RefreshUsgsEarthquakesUseCase(
    private val earthquakeRepository: EarthquakeRepository,
    private val getUsgsEarthquakeStartTimeUseCase: GetUsgsEarthquakeStartTimeUseCase
) {
    suspend operator fun invoke(
        pageSize: Int,
        selectedMagnitude: MagnitudeThreshold,
        selectedCountryBounds: CountryBounds,
        timeRange: EarthquakeTimeRange = EarthquakeTimeRange.DEFAULT,
    ) = earthquakeRepository.refresh(
        startTime = getUsgsEarthquakeStartTimeUseCase(timeRange),
        pageSize = pageSize,
        selectedMagnitude = selectedMagnitude,
        selectedCountryBounds = selectedCountryBounds,
    )
}
