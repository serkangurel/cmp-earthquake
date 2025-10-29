package com.sgmobile.earthquake.feature.earthquake.domain

import com.sgmobile.earthquake.feature.earthquake.domain.models.EarthquakeState
import org.koin.core.annotation.Factory

@Factory
internal class FetchUsgsEarthquakeDataUseCase(
    private val earthquakeRepository: EarthquakeRepository,
    private val getUsgsEarthquakeStartTimeUseCase: GetUsgsEarthquakeStartTimeUseCase
) {
    suspend operator fun invoke(): EarthquakeState {
        return earthquakeRepository.fetchUsgsEarthquakes(getUsgsEarthquakeStartTimeUseCase())
    }
}