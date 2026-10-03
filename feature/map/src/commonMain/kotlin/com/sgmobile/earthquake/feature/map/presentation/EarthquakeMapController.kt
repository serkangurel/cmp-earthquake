package com.sgmobile.earthquake.feature.map.presentation

import com.sgmobile.earthquake.core.presentation.EarthquakeFeedStore
import com.sgmobile.earthquake.core.presentation.Observation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory
class EarthquakeMapController internal constructor(store: EarthquakeFeedStore) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val mutableState = MutableStateFlow(EarthquakeMapState(store.state.value.toMapSnapshot(), null))
    internal val state = mutableState.asStateFlow()
    val currentState: EarthquakeMapState
        get() = state.value

    init {
        scope.launch {
            store.state.collect { feed ->
                val snapshot = feed.toMapSnapshot()
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

    fun selectEarthquake(id: String) {
        mutableState.update { state ->
            if (state.snapshot.pins.any { it.earthquake.id == id }) {
                state.copy(selectedEarthquakeId = id)
            } else {
                state
            }
        }
    }

    fun dismissSelection() {
        mutableState.update { it.copy(selectedEarthquakeId = null) }
    }

    fun observe(observer: (EarthquakeMapState) -> Unit): Observation {
        val job = scope.launch { state.collect(observer) }
        return object : Observation {
            override fun cancel() { job.cancel() }
        }
    }

    fun close() {
        scope.cancel()
    }
}
