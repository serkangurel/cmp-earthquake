package data

import data.model.UsgsResponse

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