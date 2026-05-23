package com.sgmobile.earthquake.feature.earthquake.detail.presentation

internal data class EarthquakeDetailUIState(
    val isLoading: Boolean = false,
) {
    companion object {
        val INITIAL = EarthquakeDetailUIState()
    }
}