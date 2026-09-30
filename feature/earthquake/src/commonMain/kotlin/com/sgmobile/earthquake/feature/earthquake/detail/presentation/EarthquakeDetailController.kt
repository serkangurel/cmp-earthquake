package com.sgmobile.earthquake.feature.earthquake.detail.presentation

import com.sgmobile.earthquake.feature.earthquake.detail.presentation.extensions.toDetailUi
import com.sgmobile.earthquake.feature.earthquake.overview.domain.GetEarthquakeFlowUseCase
import com.sgmobile.earthquake.feature.earthquake.presentation.Observation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class EarthquakeDetailController internal constructor(
    @InjectedParam private val earthquakeId: String,
    getEarthquakeFlowUseCase: GetEarthquakeFlowUseCase,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _state = MutableStateFlow(EarthquakeDetailState.INITIAL)
    internal val state: StateFlow<EarthquakeDetailState> = _state.asStateFlow()

    val currentState: EarthquakeDetailState
        get() = state.value

    init {
        scope.launch {
            getEarthquakeFlowUseCase().collect { earthquakes ->
                _state.value = EarthquakeDetailState(
                    isLoading = false,
                    earthquake = earthquakes.find { it.id == earthquakeId }?.toDetailUi(),
                )
            }
        }
    }

    fun observe(observer: (EarthquakeDetailState) -> Unit): Observation {
        val job = scope.launch {
            state.collect(observer)
        }
        return DetailJobObservation(job)
    }

    fun close() {
        scope.cancel()
    }
}

private class DetailJobObservation(
    private val job: Job,
) : Observation {
    override fun cancel() {
        job.cancel()
    }
}
