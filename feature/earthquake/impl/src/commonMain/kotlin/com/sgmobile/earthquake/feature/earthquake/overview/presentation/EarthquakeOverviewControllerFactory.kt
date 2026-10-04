package com.sgmobile.earthquake.feature.earthquake.overview.presentation

import com.sgmobile.earthquake.feature.earthquake.data.EarthquakeDatasetStore
import com.sgmobile.earthquake.feature.earthquake.overview.domain.GetCountriesUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.GetEarthquakeFlowUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.GetIsEndReachedFlowUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.LoadNextUsgsEarthquakesUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.RefreshUsgsEarthquakesUseCase
import com.sgmobile.earthquake.feature.settings.domain.SettingsSource
import org.koin.core.annotation.Factory

@Factory(binds = [EarthquakeOverviewController::class])
internal fun provideEarthquakeOverviewController(
    refreshUsgsEarthquakesUseCase: RefreshUsgsEarthquakesUseCase,
    loadNextUsgsEarthquakesUseCase: LoadNextUsgsEarthquakesUseCase,
    getEarthquakeFlowUseCase: GetEarthquakeFlowUseCase,
    getIsEndReachedFlowUseCase: GetIsEndReachedFlowUseCase,
    getCountriesUseCase: GetCountriesUseCase,
    settingsSource: SettingsSource,
    datasetStore: EarthquakeDatasetStore,
) = EarthquakeOverviewControllerImpl(
    refreshUsgsEarthquakesUseCase,
    loadNextUsgsEarthquakesUseCase,
    getEarthquakeFlowUseCase,
    getIsEndReachedFlowUseCase,
    getCountriesUseCase,
    settingsSource,
    datasetStore,
)
