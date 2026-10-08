package com.sgmobile.earthquake.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.sgmobile.earthquake.core.designsystem.DesignTokens
import com.sgmobile.earthquake.core.designsystem.MagnitudeTierTokens
import com.sgmobile.earthquake.core.designsystem.MagnitudeTokens

/**
 * Colors for one magnitude tier: [text] for labels and badges, [fill] for map marks such as pins.
 */
@Immutable
data class MagnitudeTierColors(
    val text: Color,
    val fill: Color,
) {
    /** Badge background behind [text]. */
    val tint: Color get() = text.copy(alpha = DesignTokens.MAGNITUDE_TINT_ALPHA)
}

@Immutable
data class MagnitudeColors(
    val minor: MagnitudeTierColors,
    val moderate: MagnitudeTierColors,
    val strong: MagnitudeTierColors,
)

internal val LightMagnitudeColors = DesignTokens.light.magnitude.toMagnitudeColors()

internal val DarkMagnitudeColors = DesignTokens.dark.magnitude.toMagnitudeColors()

internal val LocalMagnitudeColors = staticCompositionLocalOf { LightMagnitudeColors }

val MaterialTheme.magnitudeColors: MagnitudeColors
    @Composable
    @ReadOnlyComposable
    get() = LocalMagnitudeColors.current

private fun MagnitudeTokens.toMagnitudeColors(): MagnitudeColors = MagnitudeColors(
    minor = minor.toColors(),
    moderate = moderate.toColors(),
    strong = strong.toColors(),
)

private fun MagnitudeTierTokens.toColors(): MagnitudeTierColors =
    MagnitudeTierColors(text = Color(text), fill = Color(fill))
