package com.sgmobile.earthquake.feature.earthquake.overview.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.calf.ui.gesture.adaptiveClickable
import com.sgmobile.earthquake.core.ui.components.preview.PreviewThemes
import com.sgmobile.earthquake.core.ui.components.preview.SGPreview
import com.sgmobile.earthquake.core.ui.theme.magRed
import com.sgmobile.earthquake.core.ui.theme.magYellow
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.models.EarthquakeVo

@Composable
internal fun EarthquakeRowItem(
    model: EarthquakeVo,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val magColor = when (model.magnitudeThreshold) {
        MagnitudeThreshold.TWO_PLUS -> Color.Unspecified
        MagnitudeThreshold.FOUR_PLUS -> MaterialTheme.colorScheme.magYellow
        MagnitudeThreshold.FIVE_PLUS -> MaterialTheme.colorScheme.magRed
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .adaptiveClickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f).padding(end = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = model.place,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
            )
            Text(
                modifier = Modifier,
                text = model.date,
                style = MaterialTheme.typography.labelMedium
            )
        }
        Text(
            modifier = Modifier,
            text = model.magnitude,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = magColor,
        )
    }
}

@PreviewThemes
@Composable
private fun EarthquakeRowItemPreview() {
    SGPreview {
        EarthquakeRowItem(
            model = EarthquakeVo(
                id = "1",
                place = "San Francisco",
                magnitude = "5.2",
                magnitudeThreshold = MagnitudeThreshold.FIVE_PLUS,
                date = "19.10.2025 14:30"
            )
        )
    }
}