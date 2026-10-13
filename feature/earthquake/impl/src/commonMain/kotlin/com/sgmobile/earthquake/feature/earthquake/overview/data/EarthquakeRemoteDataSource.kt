package com.sgmobile.earthquake.feature.earthquake.overview.data

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.CountryBounds
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.Earthquake

/** One earthquake provider's paged FDSN event query, mapped to domain models. */
internal interface EarthquakeRemoteDataSource {
    suspend fun fetchPage(query: EarthquakePageQuery): Result<List<Earthquake>>
}

internal data class EarthquakePageQuery(
    val startTime: String,
    val offset: Int,
    val limit: Int,
    val minMagnitude: Double,
    val bounds: CountryBounds,
)
