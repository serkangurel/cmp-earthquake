package com.sgmobile.earthquake.core.data.extensions

import com.sgmobile.earthquake.core.data.usgs.CoordinateListItem
import com.sgmobile.earthquake.core.data.usgs.UsgsResponse
import com.sgmobile.earthquake.core.domain.models.Earthquake
import com.sgmobile.earthquake.core.domain.models.EarthquakeDateTime
import com.sgmobile.earthquake.core.domain.models.MagnitudeThreshold
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.round
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal fun UsgsResponse?.toDomainList(): List<Earthquake> {
    val response = this
    val features = response?.features.orEmpty()
    if (features.isEmpty()) return emptyList()

    return features.map { feature ->
        val place = feature.properties?.place.orEmpty()
        val magnitude = feature.properties?.mag ?: 0.0

        val magnitudeThreshold = when (magnitude) {
            in 0.0..<4.0 -> MagnitudeThreshold.TWO_PLUS
            in 4.0..<5.0 -> MagnitudeThreshold.FOUR_PLUS
            else -> MagnitudeThreshold.FIVE_PLUS
        }

        val depthKm = feature.geometry?.coordinates
            ?.getOrNull(CoordinateListItem.Depth.index)
            ?.let { depth ->
                // Depth is already in km per USGS GeoJSON (z in kilometers)
                val rounded = round(depth * 10.0) / 10.0
                if (rounded % 1.0 == 0.0) rounded.toInt().toString() else rounded.toString()
            } ?: ""

        val latitude = feature.geometry?.coordinates
            ?.getOrNull(CoordinateListItem.Latitude.index) ?: 0.0

        val longitude = feature.geometry?.coordinates
            ?.getOrNull(CoordinateListItem.Longitude.index) ?: 0.0

        val dateStr = feature.properties?.time
            ?.takeIf { it > 0L }
            ?.let { millis ->
                val instant = Instant.fromEpochMilliseconds(millis)
                val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                EarthquakeDateTime.format(local)
            } ?: ""

        val id = feature.id ?: "$latitude,$longitude,$dateStr"

        Earthquake(
            id = id,
            place = place,
            magnitude = magnitude.formattedToTwoDecimals(),
            magnitudeThreshold = magnitudeThreshold,
            depth = depthKm,
            date = dateStr,
            latitude = latitude,
            longitude = longitude
        )
    }
}
