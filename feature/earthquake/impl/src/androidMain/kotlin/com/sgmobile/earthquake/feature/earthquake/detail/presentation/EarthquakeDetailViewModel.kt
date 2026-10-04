package com.sgmobile.earthquake.feature.earthquake.detail.presentation

import androidx.lifecycle.ViewModel
import com.sgmobile.earthquake.feature.earthquake.overview.domain.GetEarthquakeFlowUseCase
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
internal class EarthquakeDetailViewModel(
    @InjectedParam earthquakeId: String,
    getEarthquakeFlowUseCase: GetEarthquakeFlowUseCase,
) : ViewModel() {
    private val controller = EarthquakeDetailControllerImpl(
        earthquakeId = earthquakeId,
        getEarthquakeFlowUseCase = getEarthquakeFlowUseCase,
    )
    val uiState = controller.state

    override fun onCleared() {
        controller.close()
    }
}
