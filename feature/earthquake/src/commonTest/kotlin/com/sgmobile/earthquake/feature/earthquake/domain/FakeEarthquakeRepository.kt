package com.sgmobile.earthquake.feature.earthquake.domain

import com.sgmobile.earthquake.feature.earthquake.domain.models.Earthquake
import com.sgmobile.earthquake.feature.earthquake.domain.models.EarthquakeState
import com.sgmobile.earthquake.feature.earthquake.domain.models.MagnitudeThreshold
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class FakeEarthquakeRepository : EarthquakeRepository {
    private val _earthquakeFlow = MutableStateFlow<List<Earthquake>>(emptyList())
    override val earthquakeFlow: StateFlow<List<Earthquake>> = _earthquakeFlow.asStateFlow()

    private val _isEndReached = MutableStateFlow(false)
    override val isEndReached: StateFlow<Boolean> = _isEndReached.asStateFlow()

    fun emitEarthquakes(earthquakes: List<Earthquake>) {
        _earthquakeFlow.value = earthquakes
    }

    override suspend fun refresh(
        startTime: String,
        pageSize: Int,
        selectedMagnitude: MagnitudeThreshold
    ): EarthquakeState {
        return EarthquakeState.Success
    }

    override suspend fun loadNextPage(): EarthquakeState {
        return EarthquakeState.Success
    }
}
