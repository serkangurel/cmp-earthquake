package com.sgmobile.earthquake.feature.earthquake.domain

import com.sgmobile.earthquake.feature.earthquake.domain.models.Earthquake
import com.sgmobile.earthquake.feature.earthquake.domain.models.EarthquakeState
import com.sgmobile.earthquake.feature.earthquake.domain.models.MagnitudeThreshold
import kotlinx.coroutines.flow.StateFlow

internal interface EarthquakeRepository {

    val earthquakeFlow: StateFlow<List<Earthquake>>
    val isEndReached: StateFlow<Boolean>

    // Resets pagination and loads the first page
    suspend fun refresh(
        startTime: String,
        pageSize: Int,
        selectedMagnitude: MagnitudeThreshold
    ): EarthquakeState

    // Loads the next page if available
    suspend fun loadNextPage(): EarthquakeState

}