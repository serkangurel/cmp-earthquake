package com.sgmobile.earthquake.feature.earthquake.data

import com.sgmobile.earthquake.feature.earthquake.data.usgs.UsgsApi
import com.sgmobile.earthquake.feature.earthquake.data.usgs.UsgsResponse
import org.koin.core.annotation.Single

@Single
class EarthquakeRepository(
    private val usgsApi: UsgsApi
) {
    suspend fun getUsgsEarthquakes(
        timeInterval: String,
    ): Result<UsgsResponse> = runCatching {
        usgsApi.getEarthquakes(timeInterval)
    }
}