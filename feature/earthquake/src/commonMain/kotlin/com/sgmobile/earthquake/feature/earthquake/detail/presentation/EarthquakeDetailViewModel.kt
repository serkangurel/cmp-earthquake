package com.sgmobile.earthquake.feature.earthquake.detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.extensions.toDetailUi
import com.sgmobile.earthquake.feature.earthquake.overview.domain.GetEarthquakeFlowUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel
import kotlin.time.Duration.Companion.seconds

@KoinViewModel
internal class EarthquakeDetailViewModel(
    @InjectedParam private val earthquakeId: String,
    getEarthquakeFlowUseCase: GetEarthquakeFlowUseCase,
) : ViewModel() {

    val uiState = getEarthquakeFlowUseCase()
        .map { earthquakes ->
            EarthquakeDetailUIState(
                isLoading = false,
                earthquake = earthquakes.find { it.id == earthquakeId }?.toDetailUi()
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5.seconds),
            EarthquakeDetailUIState.INITIAL
        )
}
