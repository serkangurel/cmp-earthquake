package com.sgmobile.earthquake.core.domain.models

sealed interface EarthquakeState {
    data object Loading : EarthquakeState

    data object Success : EarthquakeState

    data object Error : EarthquakeState
}
