package com.sgmobile.earthquake.core.data.usgs

import com.sgmobile.earthquake.core.domain.constants.EarthquakeConstants
import com.sgmobile.earthquake.core.domain.models.MagnitudeThreshold
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class UsgsApi(
    private val httpClient: HttpClient
) {
    suspend fun getEarthquakes(
        starttime: String,
        offset: Int = 1,
        limit: Int = EarthquakeConstants.PAGE_SIZE,
        minmagnitude: Double = MagnitudeThreshold.TWO_PLUS.value,
        minlatitude: Double,
        minlongitude: Double,
        maxlatitude: Double,
        maxlongitude: Double,
        format: String = "geojson",
        orderby: String = "time"
    ): Result<UsgsResponse> = runCatching {
        httpClient.get("fdsnws/event/1/query") {
            parameter("starttime", starttime)
            parameter("offset", offset)
            parameter("limit", limit)
            parameter("minmagnitude", minmagnitude)
            parameter("minlatitude", minlatitude)
            parameter("minlongitude", minlongitude)
            parameter("maxlatitude", maxlatitude)
            parameter("maxlongitude", maxlongitude)
            parameter("format", format)
            parameter("orderby", orderby)
        }.body()
    }
}
