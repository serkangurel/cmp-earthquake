package com.sgmobile.earthquake.feature.earthquake.domain

import com.sgmobile.earthquake.feature.earthquake.domain.models.Earthquake
import com.sgmobile.earthquake.feature.earthquake.domain.models.EarthquakeState
import kotlinx.coroutines.flow.StateFlow

internal interface EarthquakeRepository {

    val earthquakeFlow: StateFlow<List<Earthquake>>
    val isEndReached: StateFlow<Boolean>

    // Resets pagination and loads the first page
    suspend fun refresh(startTime: String, pageSize: Int): EarthquakeState

    // Loads the next page if available
    suspend fun loadNextPage(): EarthquakeState

}