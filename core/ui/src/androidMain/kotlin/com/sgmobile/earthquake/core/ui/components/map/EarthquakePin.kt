package com.sgmobile.earthquake.core.ui.components.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.sgmobile.earthquake.core.designsystem.DesignTokens
import com.sgmobile.earthquake.core.ui.components.preview.PreviewThemes
import com.sgmobile.earthquake.core.ui.components.preview.SGPreview
import com.sgmobile.earthquake.core.ui.theme.Spacing
import com.sgmobile.earthquake.core.ui.theme.magnitudeColors

/**
 * Earthquake map marker drawn from the shared [DesignTokens.marker] geometry, for rendering into a
 * map bitmap descriptor. [selected] adds the halo and the solid ring behind the marker.
 */
@Composable
fun EarthquakePin(color: Color, selected: Boolean = false) {
    val marker = DesignTokens.marker
    val centerColor = if (color.luminance() > marker.darkCenterLuminance) Color.Black else Color.White
    Canvas(Modifier.size(marker.canvasSize.dp)) {
        if (selected) {
            drawCircle(
                color.copy(alpha = marker.selectedHaloAlpha),
                radius = marker.selectedHaloRadius.dp.toPx(),
            )
            drawCircle(color, radius = marker.selectedRingRadius.dp.toPx())
        }
        drawCircle(
            Color.Black.copy(alpha = marker.shadowAlpha),
            radius = marker.shadowRadius.dp.toPx(),
            center = center + Offset(0f, marker.shadowOffsetY.dp.toPx()),
        )
        drawCircle(Color.White, radius = marker.ringRadius.dp.toPx())
        drawCircle(color, radius = marker.bodyRadius.dp.toPx())
        drawCircle(centerColor, radius = marker.centerRadius.dp.toPx())
        drawCircle(color, radius = marker.coreRadius.dp.toPx())
    }
}

@PreviewThemes
@Composable
private fun EarthquakePinPreview() {
    SGPreview {
        val magnitude = MaterialTheme.magnitudeColors
        Row(
            modifier = Modifier.padding(Spacing.space16),
            horizontalArrangement = Arrangement.spacedBy(Spacing.space8),
        ) {
            listOf(magnitude.minor, magnitude.moderate, magnitude.strong).forEach { tier ->
                EarthquakePin(tier.fill)
                EarthquakePin(tier.fill, selected = true)
            }
        }
    }
}
