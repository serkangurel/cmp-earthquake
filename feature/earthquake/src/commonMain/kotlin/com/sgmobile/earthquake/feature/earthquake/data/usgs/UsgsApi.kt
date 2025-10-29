package com.sgmobile.earthquake.feature.earthquake.data.usgs

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface UsgsApi {
    @GET("fdsnws/event/1/query")
    suspend fun getEarthquakes(
        @Query("starttime") starttime: String,
        @Query("offset") offset: Int = 1,
        @Query("limit") limit: Int = 30,
        @Query("format") format: String = "geojson",
        @Query("orderby") orderby: String = "time"
    ): Result<UsgsResponse>
}