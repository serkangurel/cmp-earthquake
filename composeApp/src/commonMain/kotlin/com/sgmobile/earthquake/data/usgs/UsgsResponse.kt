package com.sgmobile.earthquake.data.usgs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsgsResponse(
    @SerialName("features")
    val features: List<FeaturesResponse>? = null
)

@Serializable
data class FeaturesResponse(
    @SerialName("type")
    val type: String? = null,
    @SerialName("properties")
    val properties: PropertiesResponse? = null,
    @SerialName("geometry")
    val geometry: GeometryResponse? = null,
    @SerialName("id")
    val id: String? = null
)

@Serializable
data class PropertiesResponse(
    @SerialName("mag")
    val mag: Double? = null,
    @SerialName("place")
    val place: String? = null,
    @SerialName("time")
    val time: Long? = null
)

@Serializable
data class GeometryResponse(
    @SerialName("type")
    val type: String? = null,
    @SerialName("coordinates")
    val coordinates: List<Double>? = null
)

enum class CoordinateListItem(val index: Int) {
    Latitude(1),
    Longitude(0),
    Depth(2)
}