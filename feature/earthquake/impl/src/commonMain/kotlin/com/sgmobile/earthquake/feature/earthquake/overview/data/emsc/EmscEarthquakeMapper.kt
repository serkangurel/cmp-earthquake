package com.sgmobile.earthquake.feature.earthquake.overview.data.emsc

import com.sgmobile.earthquake.feature.earthquake.overview.data.extensions.formattedToTwoDecimals
import com.sgmobile.earthquake.feature.earthquake.overview.data.extensions.toDepthText
import com.sgmobile.earthquake.feature.earthquake.overview.data.extensions.toEarthquakeDate
import com.sgmobile.earthquake.feature.earthquake.overview.data.extensions.toMagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.Earthquake
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.EarthquakeSource
import kotlin.time.Instant

private val LOWERCASE_CONNECTORS = setOf("of", "the", "and")

internal fun EmscResponse?.toDomainList(): List<Earthquake> {
    val features = this?.features.orEmpty()
    if (features.isEmpty()) return emptyList()

    return features.map { feature ->
        val properties = feature.properties
        val magnitude = properties?.mag ?: 0.0
        val latitude = properties?.lat ?: 0.0
        val longitude = properties?.lon ?: 0.0
        // Unlike geometry.coordinates, properties.depth is a positive value in kilometers.
        val depthKm = properties?.depth?.toDepthText().orEmpty()
        val dateStr = properties?.time
            ?.let { Instant.parseOrNull(it) }
            ?.toEarthquakeDate()
            .orEmpty()

        Earthquake(
            id = properties?.unid ?: feature.id ?: "$latitude,$longitude,$dateStr",
            place = properties?.flynnRegion?.toRegionTitleCase().orEmpty(),
            magnitude = magnitude.formattedToTwoDecimals(),
            magnitudeThreshold = magnitude.toMagnitudeThreshold(),
            depth = depthKm,
            date = dateStr,
            latitude = latitude,
            longitude = longitude,
            source = EarthquakeSource.EMSC
        )
    }
}

/** EMSC sends uppercase Flinn-Engdahl region names, e.g. "SOUTH OF JAVA, INDONESIA". */
internal fun String.toRegionTitleCase(): String {
    val capitalized = buildString {
        var capitalizeNext = true
        for (char in this@toRegionTitleCase.lowercase()) {
            append(if (capitalizeNext) char.uppercaseChar() else char)
            capitalizeNext = char == ' ' || char == '-' || char == '.' || char == '('
        }
    }
    return capitalized.split(' ').mapIndexed { index, word ->
        if (index > 0 && word.lowercase() in LOWERCASE_CONNECTORS) word.lowercase() else word
    }.joinToString(" ")
}
