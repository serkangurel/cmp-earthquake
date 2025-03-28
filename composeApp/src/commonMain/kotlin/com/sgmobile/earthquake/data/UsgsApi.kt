package com.sgmobile.earthquake.data

import com.sgmobile.earthquake.data.model.UsgsResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface UsgsApi {
    @GET("earthquakes/feed/v1.0/summary/{timeInterval}.geojson")
    suspend fun getEarthquakes(@Path("timeInterval") timeInterval: String): UsgsResponse
}