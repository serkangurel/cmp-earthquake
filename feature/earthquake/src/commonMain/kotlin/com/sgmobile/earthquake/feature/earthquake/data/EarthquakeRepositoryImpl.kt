package com.sgmobile.earthquake.feature.earthquake.data

import com.sgmobile.earthquake.feature.earthquake.constants.EarthquakeConstants
import com.sgmobile.earthquake.feature.earthquake.data.extensions.toDomainList
import com.sgmobile.earthquake.feature.earthquake.data.usgs.UsgsApi
import com.sgmobile.earthquake.feature.earthquake.domain.EarthquakeRepository
import com.sgmobile.earthquake.feature.earthquake.domain.models.Earthquake
import com.sgmobile.earthquake.feature.earthquake.domain.models.EarthquakeState
import com.sgmobile.earthquake.feature.earthquake.domain.models.MagnitudeThreshold
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single

@Single(binds = [EarthquakeRepository::class])
internal class EarthquakeRepositoryImpl(
    private val usgsApi: UsgsApi
) : EarthquakeRepository {
    private val _earthquakeFlow = MutableStateFlow<List<Earthquake>>(emptyList())
    override val earthquakeFlow: StateFlow<List<Earthquake>> = _earthquakeFlow.asStateFlow()

    private val _isEndReached = MutableStateFlow(false)
    override val isEndReached: StateFlow<Boolean> = _isEndReached.asStateFlow()

    private val _isRequestInFlight = MutableStateFlow(false)

    private var startTime: String = ""
    private var offset: Int = 1
    private var pageSize: Int = EarthquakeConstants.PAGE_SIZE
    private var selectedMagnitude: MagnitudeThreshold = MagnitudeThreshold.TWO_PLUS

    override suspend fun refresh(
        startTime: String,
        pageSize: Int,
        selectedMagnitude: MagnitudeThreshold
    ): EarthquakeState {
        this.startTime = startTime
        this.pageSize = pageSize
        this.offset = 1
        this.selectedMagnitude = selectedMagnitude

        _isEndReached.value = false
        _earthquakeFlow.value = emptyList()

        return fetchPage(reset = true)
    }

    override suspend fun loadNextPage(): EarthquakeState {
        if (_isEndReached.value || _isRequestInFlight.value) return EarthquakeState.Success
        return fetchPage(reset = false)
    }

    private suspend fun fetchPage(reset: Boolean): EarthquakeState {
        _isRequestInFlight.value = true
        return usgsApi.getEarthquakes(
            starttime = startTime,
            offset = offset,
            limit = pageSize,
            minmagnitude = selectedMagnitude.value,
        ).fold(
            onSuccess = { response ->
                val page = response.toDomainList()

                val existing = _earthquakeFlow.value
                val merged = if (existing.isEmpty() || reset) {
                    page
                } else {
                    existing + page
                }

                _earthquakeFlow.value = merged

                if (page.size < pageSize) {
                    _isEndReached.value = true
                } else {
                    offset += pageSize
                }
                _isRequestInFlight.value = false
                EarthquakeState.Success
            },
            onFailure = {
                _isRequestInFlight.value = false
                EarthquakeState.Error
            }
        )
    }
}