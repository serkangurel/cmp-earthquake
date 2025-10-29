package com.sgmobile.earthquake.feature.earthquake.domain

import com.sgmobile.earthquake.feature.earthquake.domain.models.Earthquake
import com.sgmobile.earthquake.feature.earthquake.domain.models.EarthquakeState
import kotlinx.coroutines.flow.StateFlow

internal interface EarthquakeRepository {

    val earthquakeFlow: StateFlow<List<Earthquake>>

    suspend fun fetchUsgsEarthquakes(startTime: String): EarthquakeState
}