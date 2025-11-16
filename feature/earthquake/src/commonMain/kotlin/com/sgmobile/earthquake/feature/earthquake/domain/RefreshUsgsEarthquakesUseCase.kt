package com.sgmobile.earthquake.feature.earthquake.domain

import org.koin.core.annotation.Factory

@Factory
internal class RefreshUsgsEarthquakesUseCase(
    private val earthquakeRepository: EarthquakeRepository,
    private val getUsgsEarthquakeStartTimeUseCase: GetUsgsEarthquakeStartTimeUseCase
) {
    suspend operator fun invoke(pageSize: Int) = earthquakeRepository.refresh(
        startTime = getUsgsEarthquakeStartTimeUseCase(),
        pageSize = pageSize
    )
}