package com.sgmobile.earthquake.feature.earthquake.overview.data.usgs

import com.sgmobile.earthquake.feature.earthquake.constants.EarthquakeConstants
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class UsgsApi(
    private val httpClient: HttpClient
) {
    suspend fun getEarthquakes(
        starttime: String,
        offset: Int = 1,
        limit: Int = EarthquakeConstants.PAGE_SIZE,
        minmagnitude: Double = MagnitudeThreshold.TWO_PLUS.value,
        format: String = "geojson",
        orderby: String = "time"
    ): Result<UsgsResponse> = runCatching {
        httpClient.get("fdsnws/event/1/query") {
            parameter("starttime", starttime)
            parameter("offset", offset)
            parameter("limit", limit)
            parameter("minmagnitude", minmagnitude)
            parameter("format", format)
            parameter("orderby", orderby)
        }.body()
    }
}