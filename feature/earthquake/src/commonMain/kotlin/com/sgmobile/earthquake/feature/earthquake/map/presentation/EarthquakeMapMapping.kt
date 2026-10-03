package com.sgmobile.earthquake.feature.earthquake.map.presentation

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.Country
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.Earthquake
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.extensions.mapToListItems

internal fun List<Earthquake>.toMapPins(): List<EarthquakeMapPin> {
    val validEarthquakes = filter {
        it.latitude.isFinite() && it.longitude.isFinite() &&
            it.latitude in -90.0..90.0 && it.longitude in -180.0..180.0
    }.distinctBy { it.id }
    return validEarthquakes.zip(validEarthquakes.mapToListItems()) { earthquake, listItem ->
        EarthquakeMapPin(listItem, earthquake.latitude, earthquake.longitude)
    }
}

internal fun Country.toMapCountry() = EarthquakeMapCountry(
    code = code,
    name = name,
    bounds = EarthquakeMapBounds(
        minLatitude = bounds.minLatitude,
        minLongitude = bounds.minLongitude,
        maxLatitude = bounds.maxLatitude,
        maxLongitude = bounds.maxLongitude,
    ),
)
