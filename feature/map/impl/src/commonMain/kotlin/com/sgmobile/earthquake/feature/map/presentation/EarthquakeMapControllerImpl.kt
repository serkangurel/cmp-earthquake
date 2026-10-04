package com.sgmobile.earthquake.feature.map.presentation

import com.sgmobile.earthquake.feature.earthquake.domain.EarthquakeDatasetSource
import com.sgmobile.earthquake.feature.earthquake.presentation.Observation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory(binds = [EarthquakeMapController::class])
internal class EarthquakeMapControllerImpl(source: EarthquakeDatasetSource) : EarthquakeMapController {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val mutableState = MutableStateFlow(EarthquakeMapState(source.state.value.toMapSnapshot(), null))
    override val state = mutableState.asStateFlow()
    override val currentState: EarthquakeMapState
        get() = state.value

    init {
        scope.launch {
            source.state.collect { dataset ->
                val snapshot = dataset.toMapSnapshot()
                mutableState.update { previous ->
                    val sameFilters = previous.snapshot.country?.code == snapshot.country?.code &&
                        previous.snapshot.selectedMagnitude == snapshot.selectedMagnitude
                    val selection = previous.selectedEarthquakeId?.takeIf { id ->
                        sameFilters && snapshot.pins.any { it.earthquake.id == id }
                    }
                    EarthquakeMapState(snapshot, selection)
                }
            }
        }
    }

    override fun selectEarthquake(id: String) {
        mutableState.update { state ->
            if (state.snapshot.pins.any { it.earthquake.id == id }) {
                state.copy(selectedEarthquakeId = id)
            } else {
                state
            }
        }
    }

    override fun dismissSelection() {
        mutableState.update { it.copy(selectedEarthquakeId = null) }
    }

    override fun observe(observer: (EarthquakeMapState) -> Unit): Observation {
        val job = scope.launch { state.collect(observer) }
        return object : Observation {
            override fun cancel() { job.cancel() }
        }
    }

    override fun close() {
        scope.cancel()
    }
}
