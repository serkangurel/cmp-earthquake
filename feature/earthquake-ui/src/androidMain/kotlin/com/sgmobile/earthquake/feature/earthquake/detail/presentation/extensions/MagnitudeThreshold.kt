package com.sgmobile.earthquake.feature.earthquake.detail.presentation.extensions

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.sgmobile.earthquake.core.ui.theme.magRed
import com.sgmobile.earthquake.core.ui.theme.magYellow
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold

// onSurface instead of Color.Unspecified for TWO_PLUS: the map circle needs a real color.
@Composable
internal fun MagnitudeThreshold.toTierColor(): Color = when (this) {
    MagnitudeThreshold.TWO_PLUS -> MaterialTheme.colorScheme.onSurface
    MagnitudeThreshold.FOUR_PLUS -> MaterialTheme.colorScheme.magYellow
    MagnitudeThreshold.FIVE_PLUS -> MaterialTheme.colorScheme.magRed
}
