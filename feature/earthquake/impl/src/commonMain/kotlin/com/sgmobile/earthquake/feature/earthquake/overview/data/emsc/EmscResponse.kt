package com.sgmobile.earthquake.feature.earthquake.overview.data.emsc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class EmscResponse(
    @SerialName("features")
    val features: List<EmscFeatureResponse>? = null
)

@Serializable
internal data class EmscFeatureResponse(
    @SerialName("id")
    val id: String? = null,
    @SerialName("properties")
    val properties: EmscPropertiesResponse? = null
)

@Serializable
internal data class EmscPropertiesResponse(
    @SerialName("unid")
    val unid: String? = null,
    @SerialName("time")
    val time: String? = null,
    @SerialName("flynn_region")
    val flynnRegion: String? = null,
    @SerialName("lat")
    val lat: Double? = null,
    @SerialName("lon")
    val lon: Double? = null,
    @SerialName("depth")
    val depth: Double? = null,
    @SerialName("mag")
    val mag: Double? = null
)
