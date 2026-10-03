package com.sgmobile.earthquake.core.ui.components.earthquake

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.sgmobile.earthquake.core.domain.models.MagnitudeThreshold

@Composable
fun MagnitudeThreshold.toTierColor(): Color = when (this) {
    MagnitudeThreshold.TWO_PLUS -> MaterialTheme.colorScheme.onSurface
    MagnitudeThreshold.FOUR_PLUS -> MaterialTheme.colorScheme.tertiary
    MagnitudeThreshold.FIVE_PLUS -> MaterialTheme.colorScheme.error
}
