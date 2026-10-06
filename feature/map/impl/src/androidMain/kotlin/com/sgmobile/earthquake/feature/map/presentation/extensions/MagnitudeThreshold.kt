package com.sgmobile.earthquake.feature.map.presentation.extensions

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.sgmobile.earthquake.core.ui.theme.MagnitudeTierColors
import com.sgmobile.earthquake.core.ui.theme.magnitudeColors
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold

@Composable
internal fun MagnitudeThreshold.toTierColors(): MagnitudeTierColors = when (this) {
    MagnitudeThreshold.TWO_PLUS -> MaterialTheme.magnitudeColors.minor
    MagnitudeThreshold.FOUR_PLUS -> MaterialTheme.magnitudeColors.moderate
    MagnitudeThreshold.FIVE_PLUS -> MaterialTheme.magnitudeColors.strong
}
