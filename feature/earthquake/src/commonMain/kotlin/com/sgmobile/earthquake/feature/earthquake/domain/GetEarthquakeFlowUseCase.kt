package com.sgmobile.earthquake.feature.earthquake.domain

import com.sgmobile.earthquake.feature.earthquake.domain.models.Earthquake
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.Factory

@Factory
internal class GetEarthquakeFlowUseCase(
    private val earthquakeRepository: EarthquakeRepository
) {
    operator fun invoke(): StateFlow<List<Earthquake>> {
        return earthquakeRepository.earthquakeFlow
    }
}