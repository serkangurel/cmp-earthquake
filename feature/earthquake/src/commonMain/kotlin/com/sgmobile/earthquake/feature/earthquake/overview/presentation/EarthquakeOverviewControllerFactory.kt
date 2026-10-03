package com.sgmobile.earthquake.feature.earthquake.overview.presentation

import com.sgmobile.earthquake.feature.earthquake.map.presentation.EarthquakeMapSnapshotStore
import com.sgmobile.earthquake.feature.earthquake.overview.domain.GetCountriesUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.GetEarthquakeFlowUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.GetIsEndReachedFlowUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.LoadNextUsgsEarthquakesUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.RefreshUsgsEarthquakesUseCase
import org.koin.core.annotation.Factory

@Factory
internal fun provideEarthquakeOverviewController(
    refreshUsgsEarthquakesUseCase: RefreshUsgsEarthquakesUseCase,
    loadNextUsgsEarthquakesUseCase: LoadNextUsgsEarthquakesUseCase,
    getEarthquakeFlowUseCase: GetEarthquakeFlowUseCase,
    getIsEndReachedFlowUseCase: GetIsEndReachedFlowUseCase,
    getCountriesUseCase: GetCountriesUseCase,
    mapSnapshotStore: EarthquakeMapSnapshotStore,
) = EarthquakeOverviewController(
    refreshUsgsEarthquakesUseCase,
    loadNextUsgsEarthquakesUseCase,
    getEarthquakeFlowUseCase,
    getIsEndReachedFlowUseCase,
    getCountriesUseCase,
    mapSnapshotStore,
)
