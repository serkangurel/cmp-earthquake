package com.sgmobile.earthquake.core.domain.usecase

import com.sgmobile.earthquake.core.domain.repository.EarthquakeRepository
import org.koin.core.annotation.Factory

@Factory
class LoadNextUsgsEarthquakesUseCase(
    private val repository: EarthquakeRepository,
) {
    suspend operator fun invoke() = repository.loadNextPage()
}