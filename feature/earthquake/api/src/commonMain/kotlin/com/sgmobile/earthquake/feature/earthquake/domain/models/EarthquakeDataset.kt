package com.sgmobile.earthquake.feature.earthquake.domain.models

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold

data class EarthquakeDatasetBounds(
    val minLatitude: Double,
    val minLongitude: Double,
    val maxLatitude: Double,
    val maxLongitude: Double,
)

data class EarthquakeDatasetCountry(
    val code: String,
    val name: String,
    val bounds: EarthquakeDatasetBounds,
)

data class EarthquakeSummary(
    val id: String,
    val place: String,
    val magnitude: String,
    val magnitudeThreshold: MagnitudeThreshold,
    val date: String,
    val latitude: Double,
    val longitude: Double,
)

data class EarthquakeDataset(
    val country: EarthquakeDatasetCountry?,
    val selectedMagnitude: MagnitudeThreshold,
    val earthquakes: List<EarthquakeSummary>,
    val isLoading: Boolean,
) {
    companion object {
        val INITIAL = EarthquakeDataset(null, MagnitudeThreshold.TWO_PLUS, emptyList(), false)
    }
}
