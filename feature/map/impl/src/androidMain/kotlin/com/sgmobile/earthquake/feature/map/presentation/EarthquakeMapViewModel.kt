package com.sgmobile.earthquake.feature.map.presentation

import androidx.lifecycle.ViewModel
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
internal class EarthquakeMapViewModel(
    private val controller: EarthquakeMapController,
) : ViewModel() {
    val uiState = controller.state

    fun selectEarthquake(id: String) = controller.selectEarthquake(id)

    fun dismissSelection() = controller.dismissSelection()

    override fun onCleared() {
        controller.close()
    }
}
