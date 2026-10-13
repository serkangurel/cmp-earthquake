package com.sgmobile.earthquake.feature.earthquake.overview.data.extensions

import com.sgmobile.earthquake.feature.earthquake.overview.data.usgs.CoordinateListItem
import com.sgmobile.earthquake.feature.earthquake.overview.data.usgs.UsgsResponse
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.Earthquake
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.EarthquakeSource
import kotlin.time.Instant

internal fun UsgsResponse?.toDomainList(): List<Earthquake> {
    val response = this
    val features = response?.features.orEmpty()
    if (features.isEmpty()) return emptyList()

    return features.map { feature ->
        val place = feature.properties?.place.orEmpty()
        val magnitude = feature.properties?.mag ?: 0.0

        // Depth is already in km per USGS GeoJSON (z in kilometers)
        val depthKm = feature.geometry?.coordinates
            ?.getOrNull(CoordinateListItem.Depth.index)
            ?.toDepthText() ?: ""

        val latitude = feature.geometry?.coordinates
            ?.getOrNull(CoordinateListItem.Latitude.index) ?: 0.0

        val longitude = feature.geometry?.coordinates
            ?.getOrNull(CoordinateListItem.Longitude.index) ?: 0.0

        val dateStr = feature.properties?.time
            ?.takeIf { it > 0L }
            ?.let { millis -> Instant.fromEpochMilliseconds(millis).toEarthquakeDate() } ?: ""

        val id = feature.id ?: "$latitude,$longitude,$dateStr"

        Earthquake(
            id = id,
            place = place,
            magnitude = magnitude.formattedToTwoDecimals(),
            magnitudeThreshold = magnitude.toMagnitudeThreshold(),
            depth = depthKm,
            date = dateStr,
            latitude = latitude,
            longitude = longitude,
            source = EarthquakeSource.USGS
        )
    }
}
