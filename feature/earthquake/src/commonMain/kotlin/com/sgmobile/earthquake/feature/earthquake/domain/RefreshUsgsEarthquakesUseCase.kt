package com.sgmobile.earthquake.feature.earthquake.domain

import com.sgmobile.earthquake.feature.earthquake.domain.models.MagnitudeThreshold
import org.koin.core.annotation.Factory

@Factory
internal class RefreshUsgsEarthquakesUseCase(
    private val earthquakeRepository: EarthquakeRepository,
    private val getUsgsEarthquakeStartTimeUseCase: GetUsgsEarthquakeStartTimeUseCase
) {
    suspend operator fun invoke(
        pageSize: Int,
        selectedMagnitude: MagnitudeThreshold
    ) = earthquakeRepository.refresh(
        startTime = getUsgsEarthquakeStartTimeUseCase(),
        pageSize = pageSize,
        selectedMagnitude = selectedMagnitude
    )
}