package com.sgmobile.earthquake.core.designsystem

/** Spacing scale in dp/pt, named by value. */
data class SpacingTokens(
    val space2: Int,
    val space4: Int,
    val space6: Int,
    val space8: Int,
    val space12: Int,
    val space16: Int,
    val space20: Int,
    val space24: Int,
    val space32: Int,
)

/** Corner radius in dp/pt. Fully round is not a token: platforms use CircleShape / Circle. */
data class RadiusTokens(
    val sm: Int,
    val md: Int,
    val lg: Int,
    val xl: Int,
    val xxl: Int,
)
