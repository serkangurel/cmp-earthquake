package com.sgmobile.earthquake.feature.earthquake.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sgmobile.earthquake.feature.earthquake.domain.FetchUsgsEarthquakeDataUseCase
import com.sgmobile.earthquake.feature.earthquake.domain.GetEarthquakeDataUseCase
import com.sgmobile.earthquake.feature.earthquake.domain.models.Earthquake
import com.sgmobile.earthquake.feature.earthquake.presentation.extensions.mapToUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import org.koin.android.annotation.KoinViewModel
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, FormatStringsInDatetimeFormats::class)
@KoinViewModel
internal class EarthquakeViewModel(
    private val fetchUsgsEarthquakeDataUseCase: FetchUsgsEarthquakeDataUseCase,
    private val getEarthquakeDataUseCase: GetEarthquakeDataUseCase
) : ViewModel() {

    private val earthquakeFlow = getEarthquakeDataUseCase()
    private val isLoading = MutableStateFlow(false)

    val uiState = combine(
        earthquakeFlow.map(List<Earthquake>::mapToUi),
        isLoading
    ) { earthquakes, isLoading ->
        earthquakes.copy(isLoading = isLoading)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), EarthquakeUIState.INITIAL)

    private var refreshJob: Job? = null

    init {
        fetchUsgsEarthquakes()
    }

    private fun fetchUsgsEarthquakes() {
        if (refreshJob != null) return

        isLoading.update { true }
        refreshJob = viewModelScope.launch {
            val result = fetchUsgsEarthquakeDataUseCase()
            isLoading.update { false }
        }
        refreshJob?.invokeOnCompletion {
            refreshJob = null
        }
    }
}