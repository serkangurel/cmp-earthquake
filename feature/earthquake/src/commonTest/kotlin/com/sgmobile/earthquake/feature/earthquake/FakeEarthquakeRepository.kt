package com.sgmobile.earthquake.feature.earthquake

import com.sgmobile.earthquake.core.domain.models.CountryBounds
import com.sgmobile.earthquake.core.domain.models.Earthquake
import com.sgmobile.earthquake.core.domain.models.EarthquakeState
import com.sgmobile.earthquake.core.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.core.domain.repository.EarthquakeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class FakeEarthquakeRepository : EarthquakeRepository {
    private val _earthquakeFlow = MutableStateFlow<List<Earthquake>>(emptyList())
    override val earthquakeFlow: StateFlow<List<Earthquake>> = _earthquakeFlow.asStateFlow()

    private val _isEndReached = MutableStateFlow(false)
    override val isEndReached: StateFlow<Boolean> = _isEndReached.asStateFlow()

    var refreshCount: Int = 0
        private set
    var loadNextPageCount: Int = 0
        private set

    var refreshedPageSize: Int? = null
        private set
    var refreshedMagnitude: MagnitudeThreshold? = null
        private set
    var refreshedCountryBounds: CountryBounds? = null
        private set

    fun emitEarthquakes(earthquakes: List<Earthquake>) {
        _earthquakeFlow.value = earthquakes
    }

    fun setEndReached(value: Boolean) {
        _isEndReached.value = value
    }

    override suspend fun refresh(
        startTime: String,
        pageSize: Int,
        selectedMagnitude: MagnitudeThreshold,
        selectedCountryBounds: CountryBounds,
    ): EarthquakeState {
        refreshCount += 1
        refreshedPageSize = pageSize
        refreshedMagnitude = selectedMagnitude
        refreshedCountryBounds = selectedCountryBounds
        return EarthquakeState.Success
    }

    override suspend fun loadNextPage(): EarthquakeState {
        loadNextPageCount += 1
        return EarthquakeState.Success
    }
}
