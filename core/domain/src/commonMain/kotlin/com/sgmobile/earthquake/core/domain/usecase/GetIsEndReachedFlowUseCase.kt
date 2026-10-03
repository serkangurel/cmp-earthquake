package com.sgmobile.earthquake.core.domain.usecase

import com.sgmobile.earthquake.core.domain.repository.EarthquakeRepository
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.Factory

@Factory
class GetIsEndReachedFlowUseCase(
    private val repository: EarthquakeRepository
) {
    operator fun invoke(): StateFlow<Boolean> {
        return repository.isEndReached
    }
}