package com.sgmobile.earthquake.feature.map.presentation

import com.sgmobile.earthquake.feature.earthquake.domain.models.EarthquakeDataset
import com.sgmobile.earthquake.feature.earthquake.domain.models.EarthquakeDatasetCountry

internal fun EarthquakeDataset.toMapSnapshot() = EarthquakeMapSnapshot(
    country = country?.toMapCountry(),
    selectedMagnitude = selectedMagnitude,
    pins = earthquakes.filter {
        it.latitude.isFinite() && it.longitude.isFinite() &&
            it.latitude in -90.0..90.0 && it.longitude in -180.0..180.0
    }.distinctBy { it.id }.map { earthquake ->
        EarthquakeMapPin(
            earthquake = EarthquakeMapItem(
                id = earthquake.id,
                place = earthquake.place,
                magnitude = earthquake.magnitude,
                magnitudeThreshold = earthquake.magnitudeThreshold,
                date = earthquake.date,
            ),
            latitude = earthquake.latitude,
            longitude = earthquake.longitude,
        )
    },
    isLoading = isLoading,
)

private fun EarthquakeDatasetCountry.toMapCountry() = EarthquakeMapCountry(
    code = code,
    name = name,
    bounds = EarthquakeMapBounds(
        minLatitude = bounds.minLatitude,
        minLongitude = bounds.minLongitude,
        maxLatitude = bounds.maxLatitude,
        maxLongitude = bounds.maxLongitude,
    ),
)
