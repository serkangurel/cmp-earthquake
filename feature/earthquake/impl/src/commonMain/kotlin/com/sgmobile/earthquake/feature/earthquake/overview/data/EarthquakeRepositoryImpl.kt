package com.sgmobile.earthquake.feature.earthquake.overview.data

import com.sgmobile.earthquake.feature.earthquake.constants.EarthquakeConstants
import com.sgmobile.earthquake.feature.earthquake.overview.data.emsc.EmscEarthquakeDataSource
import com.sgmobile.earthquake.feature.earthquake.overview.data.usgs.UsgsEarthquakeDataSource
import com.sgmobile.earthquake.feature.earthquake.overview.domain.EarthquakeRepository
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.CountryBounds
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.Earthquake
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.EarthquakeSource
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.EarthquakeState
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single

@Single(binds = [EarthquakeRepository::class])
internal class EarthquakeRepositoryImpl(
    private val usgsDataSource: UsgsEarthquakeDataSource,
    private val emscDataSource: EmscEarthquakeDataSource,
) : EarthquakeRepository {
    private val _earthquakeFlow = MutableStateFlow<List<Earthquake>>(emptyList())
    override val earthquakeFlow: StateFlow<List<Earthquake>> = _earthquakeFlow.asStateFlow()

    private val _isEndReached = MutableStateFlow(false)
    override val isEndReached: StateFlow<Boolean> = _isEndReached.asStateFlow()

    private val _isRequestInFlight = MutableStateFlow(false)

    private var selectedSource: EarthquakeSource = EarthquakeSource.USGS
    private var startTime: String = ""
    private var offset: Int = 1
    private var pageSize: Int = EarthquakeConstants.PAGE_SIZE
    private var selectedMagnitude: MagnitudeThreshold = MagnitudeThreshold.TWO_PLUS
    private lateinit var selectedCountryBounds: CountryBounds

    override suspend fun refresh(
        source: EarthquakeSource,
        startTime: String,
        pageSize: Int,
        selectedMagnitude: MagnitudeThreshold,
        selectedCountryBounds: CountryBounds,
    ): EarthquakeState {
        this.selectedSource = source
        this.startTime = startTime
        this.pageSize = pageSize
        this.offset = 1
        this.selectedMagnitude = selectedMagnitude
        this.selectedCountryBounds = selectedCountryBounds

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
        val query = EarthquakePageQuery(
            startTime = startTime,
            offset = offset,
            limit = pageSize,
            minMagnitude = selectedMagnitude.value,
            bounds = selectedCountryBounds,
        )
        return dataSourceFor(selectedSource).fetchPage(query).fold(
            onSuccess = { page ->
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

    private fun dataSourceFor(source: EarthquakeSource): EarthquakeRemoteDataSource = when (source) {
        EarthquakeSource.USGS -> usgsDataSource
        EarthquakeSource.EMSC -> emscDataSource
    }
}
