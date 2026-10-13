package com.sgmobile.earthquake.core.designsystem

/** Map marker (pin) geometry in dp/pt, drawn identically on both platforms. */
data class MarkerTokens(
    val canvasSize: Int,
    val bodyRadius: Int,
    val ringRadius: Int,
    val centerRadius: Int,
    val coreRadius: Int,
    /** Radius of the hard-edged shadow disc (not a blur), drawn [shadowOffsetY] below center. */
    val shadowRadius: Int,
    val shadowOffsetY: Int,
    val shadowAlpha: Float,
    val selectedHaloRadius: Int,
    val selectedHaloAlpha: Float,
    val selectedRingRadius: Int,
    /** The center dot is black when the fill's relative luminance exceeds this, white otherwise. */
    val darkCenterLuminance: Float,
)
