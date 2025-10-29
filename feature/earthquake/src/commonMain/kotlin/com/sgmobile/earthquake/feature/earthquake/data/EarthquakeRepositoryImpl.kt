package com.sgmobile.earthquake.feature.earthquake.data

import com.sgmobile.earthquake.feature.earthquake.data.extensions.toDomainList
import com.sgmobile.earthquake.feature.earthquake.data.usgs.UsgsApi
import com.sgmobile.earthquake.feature.earthquake.domain.EarthquakeRepository
import com.sgmobile.earthquake.feature.earthquake.domain.models.Earthquake
import com.sgmobile.earthquake.feature.earthquake.domain.models.EarthquakeState
import kotlinx.coroutines.delay
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

    override suspend fun fetchUsgsEarthquakes(startTime: String): EarthquakeState {
        delay(500L)
        return usgsApi.getEarthquakes(startTime).fold(
            onSuccess = { response ->
                _earthquakeFlow.value = _earthquakeFlow.value.toMutableList().apply {
                    addAll(response.toDomainList())
                }
                EarthquakeState.Success
            },
            onFailure = {
                EarthquakeState.Error
            }
        )
    }
}