package com.sgmobile.earthquake.feature.earthquake.domain.models

sealed interface EarthquakeState {
    data object Loading : EarthquakeState

    data object Success : EarthquakeState

    data object Error : EarthquakeState
}