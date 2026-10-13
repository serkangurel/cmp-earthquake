package com.sgmobile.earthquake.feature.earthquake.overview.data.usgs

import com.sgmobile.earthquake.feature.earthquake.overview.data.EarthquakePageQuery
import com.sgmobile.earthquake.feature.earthquake.overview.data.EarthquakeRemoteDataSource
import com.sgmobile.earthquake.feature.earthquake.overview.data.extensions.toDomainList
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.Earthquake
import org.koin.core.annotation.Single

@Single
internal class UsgsEarthquakeDataSource(
    private val usgsApi: UsgsApi,
) : EarthquakeRemoteDataSource {
    override suspend fun fetchPage(query: EarthquakePageQuery): Result<List<Earthquake>> =
        usgsApi.getEarthquakes(
            starttime = query.startTime,
            offset = query.offset,
            limit = query.limit,
            minmagnitude = query.minMagnitude,
            minlatitude = query.bounds.minLatitude,
            minlongitude = query.bounds.minLongitude,
            maxlatitude = query.bounds.maxLatitude,
            maxlongitude = query.bounds.maxLongitude,
        ).map { it.toDomainList() }
}
