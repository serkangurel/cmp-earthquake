package com.sgmobile.earthquake.feature.earthquake.overview.presentation

import androidx.lifecycle.ViewModel
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class EarthquakeViewModel(
    private val controller: EarthquakeOverviewController,
) : ViewModel() {
    val uiState = controller.state

    fun handleIntent(intent: EarthquakeScreenIntent) {
        when (intent) {
            is EarthquakeScreenIntent.Refresh -> controller.refresh()
            is EarthquakeScreenIntent.LoadMore -> controller.loadMore()
            is EarthquakeScreenIntent.SelectMagnitude ->
                controller.selectMagnitude(intent.selectedMagnitude)
            is EarthquakeScreenIntent.SelectCountry ->
                controller.selectCountry(intent.country.code)
        }
    }

    override fun onCleared() {
        controller.close()
    }
}
