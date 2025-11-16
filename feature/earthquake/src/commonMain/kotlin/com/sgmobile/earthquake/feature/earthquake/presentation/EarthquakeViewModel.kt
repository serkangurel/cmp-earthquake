package com.sgmobile.earthquake.feature.earthquake.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sgmobile.earthquake.feature.earthquake.constants.EarthquakeConstants
import com.sgmobile.earthquake.feature.earthquake.domain.GetEarthquakeFlowUseCase
import com.sgmobile.earthquake.feature.earthquake.domain.GetIsEndReachedFlowUseCase
import com.sgmobile.earthquake.feature.earthquake.domain.LoadNextUsgsEarthquakesUseCase
import com.sgmobile.earthquake.feature.earthquake.domain.RefreshUsgsEarthquakesUseCase
import com.sgmobile.earthquake.feature.earthquake.domain.models.Earthquake
import com.sgmobile.earthquake.feature.earthquake.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.presentation.extensions.mapToUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import kotlin.time.Duration.Companion.seconds

@KoinViewModel
internal class EarthquakeViewModel(
    private val refreshUsgsEarthquakesUseCase: RefreshUsgsEarthquakesUseCase,
    private val loadNextUsgsEarthquakesUseCase: LoadNextUsgsEarthquakesUseCase,
    private val getEarthquakeFlowUseCase: GetEarthquakeFlowUseCase,
    private val getIsEndReachedFlowUseCase: GetIsEndReachedFlowUseCase,
) : ViewModel() {

    private val earthquakeFlow = getEarthquakeFlowUseCase()
    private val isLoadingFlow = MutableStateFlow(false)
    private val isPullToRefreshFlow = MutableStateFlow(false)
    private val isEndReachedFlow = getIsEndReachedFlowUseCase()
    private val selectedMagnitudeFlow = MutableStateFlow(MagnitudeThreshold.TWO_PLUS)

    val uiState = combine(
        earthquakeFlow.map(List<Earthquake>::mapToUi),
        isLoadingFlow,
        isPullToRefreshFlow,
        isEndReachedFlow,
        selectedMagnitudeFlow
    ) { earthquakes, isLoading, isPullToRefresh, isEndReached, selectedMagnitude ->
        earthquakes.copy(
            isLoading = isLoading,
            isPullToRefresh = isPullToRefresh,
            isEndReached = isEndReached,
            selectedMagnitude = selectedMagnitude
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5.seconds),
        EarthquakeUIState.INITIAL
    )

    init {
        refresh(isPullToRefresh = false)
    }

    fun handleIntent(intent: EarthquakeScreenIntent) {
        when (intent) {
            is EarthquakeScreenIntent.Refresh -> refresh(isPullToRefresh = intent.isPullToRefresh)
            is EarthquakeScreenIntent.LoadMore -> loadMore()
            is EarthquakeScreenIntent.SelectMagnitude -> selectMagnitude(intent.selectedMagnitude)
        }
    }

    private fun selectMagnitude(selectedMagnitude: MagnitudeThreshold) {
        if (selectedMagnitude == uiState.value.selectedMagnitude) return
        selectedMagnitudeFlow.value = selectedMagnitude
        refresh(isPullToRefresh = false)
    }

    private fun refresh(
        isPullToRefresh: Boolean,
        pageSize: Int = EarthquakeConstants.PAGE_SIZE,
        selectedMagnitude: MagnitudeThreshold = this.selectedMagnitudeFlow.value
    ) {
        executeWithLoading(isPullToRefresh = isPullToRefresh) {
            refreshUsgsEarthquakesUseCase(pageSize, selectedMagnitude)
        }
    }

    private fun loadMore() {
        if (!isEndReachedFlow.value) {
            executeWithLoading(isPullToRefresh = false) {
                loadNextUsgsEarthquakesUseCase()
            }
        }
    }

    private fun <T> executeWithLoading(
        isPullToRefresh: Boolean,
        block: suspend () -> T
    ) {
        viewModelScope.launch {
            updateLoadingFlows(isPullToRefresh = isPullToRefresh, value = true)
            try {
                block()
            } finally {
                updateLoadingFlows(isPullToRefresh = isPullToRefresh, value = false)
            }
        }
    }

    private fun updateLoadingFlows(isPullToRefresh: Boolean, value: Boolean) {
        if (isPullToRefresh) {
            isPullToRefreshFlow.value = value
        } else {
            isLoadingFlow.value = value
        }
    }
}