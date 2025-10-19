package com.sgmobile.earthquake.feature.earthquake.domain

import com.sgmobile.earthquake.feature.earthquake.data.usgs.UsgsResponse

interface EarthquakeRepository {
    suspend fun getUsgsEarthquakes(timeInterval: String): Result<UsgsResponse>
}