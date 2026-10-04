package com.sgmobile.earthquake.feature.earthquake.overview.domain.models

internal sealed interface EarthquakeState {
    data object Loading : EarthquakeState

    data object Success : EarthquakeState

    data object Error : EarthquakeState
}
