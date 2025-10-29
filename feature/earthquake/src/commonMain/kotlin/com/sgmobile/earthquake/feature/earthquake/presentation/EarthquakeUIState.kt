package com.sgmobile.earthquake.feature.earthquake.presentation

import com.sgmobile.earthquake.feature.earthquake.presentation.models.EarthquakeVo

internal data class EarthquakeUIState(
    val isLoading: Boolean,
    val earhtquakeList: List<EarthquakeVo>
) {
    companion object Companion {
        val INITIAL = EarthquakeUIState(
            isLoading = false,
            earhtquakeList = listOf()
        )
    }
}