package com.sgmobile.earthquake.core.designsystem

/** One theme's colors as ARGB values (0xAARRGGBB). */
data class ColorTokens(
    val primary: Long,
    val onPrimary: Long,
    val primaryContainer: Long,
    val onPrimaryContainer: Long,
    val secondary: Long,
    val onSecondary: Long,
    val secondaryContainer: Long,
    val onSecondaryContainer: Long,
    val tertiary: Long,
    val onTertiary: Long,
    val tertiaryContainer: Long,
    val onTertiaryContainer: Long,
    val error: Long,
    val onError: Long,
    val errorContainer: Long,
    val onErrorContainer: Long,
    val background: Long,
    val onBackground: Long,
    val surface: Long,
    val onSurface: Long,
    val surfaceVariant: Long,
    val onSurfaceVariant: Long,
    val outline: Long,
    val outlineVariant: Long,
    val scrim: Long,
    val inverseSurface: Long,
    val inverseOnSurface: Long,
    val inversePrimary: Long,
    val surfaceDim: Long,
    val surfaceBright: Long,
    val surfaceContainerLowest: Long,
    val surfaceContainerLow: Long,
    val surfaceContainer: Long,
    val surfaceContainerHigh: Long,
    val surfaceContainerHighest: Long,
    val magnitude: MagnitudeTokens,
)

data class MagnitudeTokens(
    val minor: MagnitudeTierTokens,
    val moderate: MagnitudeTierTokens,
    val strong: MagnitudeTierTokens,
)

/** [text] for labels and badges, [fill] for map pins and markers. */
data class MagnitudeTierTokens(val text: Long, val fill: Long)
