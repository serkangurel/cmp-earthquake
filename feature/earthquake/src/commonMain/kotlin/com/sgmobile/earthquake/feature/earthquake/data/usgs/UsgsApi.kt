package com.sgmobile.earthquake.feature.earthquake.data.usgs

import com.sgmobile.earthquake.feature.earthquake.constants.EarthquakeConstants
import com.sgmobile.earthquake.feature.earthquake.domain.models.MagnitudeThreshold
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface UsgsApi {
    @GET("fdsnws/event/1/query")
    suspend fun getEarthquakes(
        @Query("starttime") starttime: String,
        @Query("offset") offset: Int = 1,
        @Query("limit") limit: Int = EarthquakeConstants.PAGE_SIZE,
        @Query("minmagnitude") minmagnitude: Double = MagnitudeThreshold.TWO_PLUS.value,
        @Query("format") format: String = "geojson",
        @Query("orderby") orderby: String = "time"
    ): Result<UsgsResponse>
}