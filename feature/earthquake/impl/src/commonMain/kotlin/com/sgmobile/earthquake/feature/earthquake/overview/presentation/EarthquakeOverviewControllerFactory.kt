package com.sgmobile.earthquake.feature.earthquake.overview.presentation

import com.sgmobile.earthquake.feature.earthquake.data.EarthquakeDatasetStore
import com.sgmobile.earthquake.feature.earthquake.overview.domain.GetCountriesUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.GetEarthquakeFlowUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.GetIsEndReachedFlowUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.LoadNextEarthquakesUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.RefreshEarthquakesUseCase
import org.koin.core.annotation.Factory

@Factory(binds = [EarthquakeOverviewController::class])
internal fun provideEarthquakeOverviewController(
    refreshEarthquakesUseCase: RefreshEarthquakesUseCase,
    loadNextEarthquakesUseCase: LoadNextEarthquakesUseCase,
    getEarthquakeFlowUseCase: GetEarthquakeFlowUseCase,
    getIsEndReachedFlowUseCase: GetIsEndReachedFlowUseCase,
    getCountriesUseCase: GetCountriesUseCase,
    datasetStore: EarthquakeDatasetStore,
) = EarthquakeOverviewControllerImpl(
    refreshEarthquakesUseCase,
    loadNextEarthquakesUseCase,
    getEarthquakeFlowUseCase,
    getIsEndReachedFlowUseCase,
    getCountriesUseCase,
    datasetStore,
)
