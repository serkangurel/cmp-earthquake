package com.sgmobile.earthquake.feature.earthquake.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sgmobile.earthquake.feature.earthquake.constants.EarthquakeConstants
import com.sgmobile.earthquake.feature.earthquake.domain.GetEarthquakeFlowUseCase
import com.sgmobile.earthquake.feature.earthquake.domain.GetIsEndReachedFlowUseCase
import com.sgmobile.earthquake.feature.earthquake.domain.LoadNextUsgsEarthquakesUseCase
import com.sgmobile.earthquake.feature.earthquake.domain.RefreshUsgsEarthquakesUseCase
import com.sgmobile.earthquake.feature.earthquake.domain.models.Earthquake
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
    private val isEndReachedFlow = getIsEndReachedFlowUseCase()

    val uiState = combine(
        earthquakeFlow.map(List<Earthquake>::mapToUi),
        isLoadingFlow,
        isEndReachedFlow
    ) { earthquakes, isLoading, isEndReached ->
        earthquakes.copy(
            isLoading = isLoading,
            isEndReached = isEndReached
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5.seconds),
        EarthquakeUIState.INITIAL
    )

    init {
        refresh()
    }

    fun handleIntent(intent: EarthquakeScreenIntent) {
        when (intent) {
            EarthquakeScreenIntent.Refresh -> refresh()
            EarthquakeScreenIntent.LoadMore -> loadMore()
        }
    }

    fun refresh(pageSize: Int = EarthquakeConstants.PAGE_SIZE) {
        viewModelScope.launch {
            isLoadingFlow.value = true
            refreshUsgsEarthquakesUseCase(pageSize)
            isLoadingFlow.value = false
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            if (!isEndReachedFlow.value) {
                isLoadingFlow.value = true
                loadNextUsgsEarthquakesUseCase()
                isLoadingFlow.value = false
            }
        }
    }

}