package com.sgmobile.earthquake.feature.earthquake.domain

import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.Factory

@Factory
internal class GetIsEndReachedFlowUseCase(
    private val repository: EarthquakeRepository
) {
    operator fun invoke(): StateFlow<Boolean> {
        return repository.isEndReached
    }
}