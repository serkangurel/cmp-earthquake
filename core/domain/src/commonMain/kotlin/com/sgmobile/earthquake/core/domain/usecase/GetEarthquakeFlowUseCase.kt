package com.sgmobile.earthquake.core.domain.usecase

import com.sgmobile.earthquake.core.domain.models.Earthquake
import com.sgmobile.earthquake.core.domain.repository.EarthquakeRepository
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.Factory

@Factory
class GetEarthquakeFlowUseCase(
    private val earthquakeRepository: EarthquakeRepository
) {
    operator fun invoke(): StateFlow<List<Earthquake>> {
        return earthquakeRepository.earthquakeFlow
    }
}