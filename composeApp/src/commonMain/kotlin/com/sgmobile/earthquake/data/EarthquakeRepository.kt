package com.sgmobile.earthquake.data

import com.sgmobile.earthquake.data.model.UsgsResponse

class EarthquakeRepository(
    private val usgsApi: UsgsApi
) {
    suspend fun getUsgsEarthquakes(
        timeInterval: String,
        onSuccess: (UsgsResponse) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        runCatching {
            usgsApi.getEarthquakes(timeInterval)
        }.onSuccess {
            onSuccess.invoke(it)
        }.onFailure {
            onFailure.invoke(it)
        }

    }
}