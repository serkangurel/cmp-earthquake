package com.sgmobile.earthquake.feature.earthquake.detail.presentation

import com.sgmobile.earthquake.feature.earthquake.detail.presentation.models.EarthquakeDetailVo

internal data class EarthquakeDetailUIState(
    val isLoading: Boolean = false,
    val earthquake: EarthquakeDetailVo? = null,
) {
    companion object {
        val INITIAL = EarthquakeDetailUIState(isLoading = true)
    }
}
