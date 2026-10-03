package com.sgmobile.earthquake.core.domain.repository

import com.sgmobile.earthquake.core.domain.models.CountryBounds
import com.sgmobile.earthquake.core.domain.models.Earthquake
import com.sgmobile.earthquake.core.domain.models.EarthquakeState
import com.sgmobile.earthquake.core.domain.models.MagnitudeThreshold
import kotlinx.coroutines.flow.StateFlow

interface EarthquakeRepository {

    val earthquakeFlow: StateFlow<List<Earthquake>>
    val isEndReached: StateFlow<Boolean>

    // Resets pagination and loads the first page
    suspend fun refresh(
        startTime: String,
        pageSize: Int,
        selectedMagnitude: MagnitudeThreshold,
        selectedCountryBounds: CountryBounds,
    ): EarthquakeState

    // Loads the next page if available
    suspend fun loadNextPage(): EarthquakeState

}
