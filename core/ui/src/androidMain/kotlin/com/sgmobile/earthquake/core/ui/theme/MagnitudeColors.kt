package com.sgmobile.earthquake.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private const val MAGNITUDE_TINT_ALPHA = 0.12f

/**
 * Colors for one magnitude tier: [text] for labels and badges, [fill] for map marks such as pins.
 */
@Immutable
data class MagnitudeTierColors(
    val text: Color,
    val fill: Color,
) {
    /** Badge background behind [text]. */
    val tint: Color get() = text.copy(alpha = MAGNITUDE_TINT_ALPHA)
}

@Immutable
data class MagnitudeColors(
    val minor: MagnitudeTierColors,
    val moderate: MagnitudeTierColors,
    val strong: MagnitudeTierColors,
)

// Keep in sync with iosApp AppColors.magnitudeText / magnitudeFill.
internal val LightMagnitudeColors = MagnitudeColors(
    minor = MagnitudeTierColors(text = onSurfaceLight, fill = onSurfaceLight),
    moderate = MagnitudeTierColors(text = Color(0xFF8D5500), fill = Color(0xFFFFB300)),
    strong = MagnitudeTierColors(text = Color(0xFFB42323), fill = Color(0xFFD32F2F)),
)

internal val DarkMagnitudeColors = MagnitudeColors(
    minor = MagnitudeTierColors(text = onSurfaceDark, fill = onSurfaceDark),
    moderate = MagnitudeTierColors(text = Color(0xFFFFBE40), fill = Color(0xFFFFB300)),
    strong = MagnitudeTierColors(text = Color(0xFFFF8880), fill = Color(0xFFE53935)),
)

internal val LocalMagnitudeColors = staticCompositionLocalOf { LightMagnitudeColors }

val MaterialTheme.magnitudeColors: MagnitudeColors
    @Composable
    @ReadOnlyComposable
    get() = LocalMagnitudeColors.current
