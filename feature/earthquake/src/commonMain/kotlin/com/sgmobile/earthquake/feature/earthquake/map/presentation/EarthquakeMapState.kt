package com.sgmobile.earthquake.feature.earthquake.map.presentation

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.models.EarthquakeListItem

data class EarthquakeMapBounds(
    val minLatitude: Double,
    val minLongitude: Double,
    val maxLatitude: Double,
    val maxLongitude: Double,
)

data class EarthquakeMapCountry(
    val code: String,
    val name: String,
    val bounds: EarthquakeMapBounds,
)

data class EarthquakeMapPin(
    val earthquake: EarthquakeListItem,
    val latitude: Double,
    val longitude: Double,
)

data class EarthquakeMapSnapshot(
    val country: EarthquakeMapCountry?,
    val selectedMagnitude: MagnitudeThreshold,
    val pins: List<EarthquakeMapPin>,
    val isLoading: Boolean,
) {
    companion object {
        val INITIAL = EarthquakeMapSnapshot(null, MagnitudeThreshold.TWO_PLUS, emptyList(), false)
    }
}

data class EarthquakeMapState(
    val snapshot: EarthquakeMapSnapshot,
    val selectedEarthquakeId: String?,
) {
    val selectedEarthquake: EarthquakeMapPin?
        get() = snapshot.pins.firstOrNull { it.earthquake.id == selectedEarthquakeId }

    companion object {
        val INITIAL = EarthquakeMapState(EarthquakeMapSnapshot.INITIAL, null)
    }
}
