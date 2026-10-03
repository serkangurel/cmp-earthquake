package com.sgmobile.earthquake.feature.earthquake.detail.presentation.extensions

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold

@Composable
fun MagnitudeThreshold.toTierColor(): Color = when (this) {
    MagnitudeThreshold.TWO_PLUS -> MaterialTheme.colorScheme.onSurface
    MagnitudeThreshold.FOUR_PLUS -> MaterialTheme.colorScheme.tertiary
    MagnitudeThreshold.FIVE_PLUS -> MaterialTheme.colorScheme.error
}
