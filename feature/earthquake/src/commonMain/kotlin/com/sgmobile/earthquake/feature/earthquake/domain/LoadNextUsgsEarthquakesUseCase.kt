package com.sgmobile.earthquake.feature.earthquake.domain

import org.koin.core.annotation.Factory

@Factory
internal class LoadNextUsgsEarthquakesUseCase(
    private val repository: EarthquakeRepository,
) {
    suspend operator fun invoke() = repository.loadNextPage()
}