package com.sgmobile.earthquake.feature.earthquake.overview.data.emsc

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess

internal class EmscApi(
    private val httpClient: HttpClient
) {
    suspend fun getEarthquakes(
        starttime: String,
        offset: Int,
        limit: Int,
        minmagnitude: Double,
        minlatitude: Double,
        minlongitude: Double,
        maxlatitude: Double,
        maxlongitude: Double,
        format: String = "json",
        orderby: String = "time"
    ): Result<EmscResponse> = runCatching {
        val response = httpClient.get("fdsnws/event/1/query") {
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
        }
        when {
            // EMSC answers "no matching events" (including an offset past the end) with an empty 204.
            response.status == HttpStatusCode.NoContent -> EmscResponse(features = emptyList())
            !response.status.isSuccess() -> error("EMSC request failed with ${response.status}")
            else -> response.body()
        }
    }
}
