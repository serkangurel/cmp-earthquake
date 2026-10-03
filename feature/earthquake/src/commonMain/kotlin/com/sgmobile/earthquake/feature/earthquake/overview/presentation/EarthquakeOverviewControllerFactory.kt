package com.sgmobile.earthquake.feature.earthquake.overview.presentation

import com.sgmobile.earthquake.core.domain.usecase.GetCountriesUseCase
import com.sgmobile.earthquake.core.domain.usecase.GetEarthquakeFlowUseCase
import com.sgmobile.earthquake.core.domain.usecase.GetIsEndReachedFlowUseCase
import com.sgmobile.earthquake.core.domain.usecase.LoadNextUsgsEarthquakesUseCase
import com.sgmobile.earthquake.core.domain.usecase.RefreshUsgsEarthquakesUseCase
import com.sgmobile.earthquake.core.presentation.EarthquakeFeedStore
import org.koin.core.annotation.Factory

@Factory
internal fun provideEarthquakeOverviewController(
    refreshUsgsEarthquakesUseCase: RefreshUsgsEarthquakesUseCase,
    loadNextUsgsEarthquakesUseCase: LoadNextUsgsEarthquakesUseCase,
    getEarthquakeFlowUseCase: GetEarthquakeFlowUseCase,
    getIsEndReachedFlowUseCase: GetIsEndReachedFlowUseCase,
    getCountriesUseCase: GetCountriesUseCase,
    feedStore: EarthquakeFeedStore,
) = EarthquakeOverviewController(
    refreshUsgsEarthquakesUseCase,
    loadNextUsgsEarthquakesUseCase,
    getEarthquakeFlowUseCase,
    getIsEndReachedFlowUseCase,
    getCountriesUseCase,
    feedStore,
)
