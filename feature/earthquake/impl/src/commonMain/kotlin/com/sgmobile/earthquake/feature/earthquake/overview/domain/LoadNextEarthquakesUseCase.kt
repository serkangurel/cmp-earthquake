package com.sgmobile.earthquake.feature.earthquake.overview.domain

import org.koin.core.annotation.Factory

@Factory
internal class LoadNextEarthquakesUseCase(
    private val repository: EarthquakeRepository,
) {
    suspend operator fun invoke() = repository.loadNextPage()
}