package com.sgmobile.earthquake.feature.earthquake.overview.presentation

import androidx.lifecycle.ViewModel
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
internal class EarthquakeViewModel(
    private val controller: EarthquakeOverviewController,
) : ViewModel() {
    val uiState = controller.state

    fun handleIntent(intent: EarthquakeScreenIntent) = controller.onIntent(intent)

    override fun onCleared() {
        controller.close()
    }
}
