package com.sgmobile.earthquake.feature.earthquake.data

import com.sgmobile.earthquake.feature.earthquake.data.usgs.UsgsApi
import com.sgmobile.earthquake.feature.earthquake.data.usgs.UsgsResponse
import com.sgmobile.earthquake.feature.earthquake.domain.EarthquakeRepository
import org.koin.core.annotation.Factory

@Factory(binds = [EarthquakeRepository::class])
internal class EarthquakeRepositoryImpl(
    private val usgsApi: UsgsApi
) : EarthquakeRepository {
    override suspend fun getUsgsEarthquakes(
        timeInterval: String,
    ): Result<UsgsResponse> = usgsApi.getEarthquakes(timeInterval)
}