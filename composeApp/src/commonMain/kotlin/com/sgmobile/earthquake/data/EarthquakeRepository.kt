package com.sgmobile.earthquake.data

import com.sgmobile.earthquake.data.model.UsgsResponse

class EarthquakeRepository(
    private val usgsApi: UsgsApi
) {
    suspend fun getUsgsEarthquakes(
        timeInterval: String,
    ): Result<UsgsResponse> = runCatching {
        usgsApi.getEarthquakes(timeInterval)
    }
}