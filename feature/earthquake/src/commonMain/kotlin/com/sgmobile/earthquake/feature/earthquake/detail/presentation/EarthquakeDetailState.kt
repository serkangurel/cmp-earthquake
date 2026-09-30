package com.sgmobile.earthquake.feature.earthquake.detail.presentation

import com.sgmobile.earthquake.feature.earthquake.detail.presentation.models.EarthquakeDetail

data class EarthquakeDetailState(
    val isLoading: Boolean = false,
    val earthquake: EarthquakeDetail? = null,
) {
    companion object {
        val INITIAL = EarthquakeDetailState(isLoading = true)
    }
}
