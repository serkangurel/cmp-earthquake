package com.sgmobile.earthquake.feature.map.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sgmobile.earthquake.core.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.core.presentation.models.EarthquakeListItem
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.dismiss_earthquake_summary
import com.sgmobile.earthquake.core.resource.view_earthquake_details
import com.sgmobile.earthquake.core.ui.components.earthquake.EarthquakeTimestampLabel
import com.sgmobile.earthquake.core.ui.components.earthquake.toTierColor
import com.sgmobile.earthquake.core.ui.components.preview.PreviewThemes
import com.sgmobile.earthquake.core.ui.components.preview.SGPreview
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EarthquakeMapSummary(
    earthquake: EarthquakeListItem,
    onDismiss: () -> Unit,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 6.dp,
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = earthquake.magnitude,
                    color = earthquake.magnitudeThreshold.toTierColor(),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(earthquake.place, style = MaterialTheme.typography.titleMedium)
                    EarthquakeTimestampLabel(earthquake.timestamp)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextButton(onClick = onViewDetails) {
                    Text(stringResource(Res.string.view_earthquake_details))
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Filled.Close, stringResource(Res.string.dismiss_earthquake_summary))
                }
            }
        }
    }
}

@PreviewThemes
@Preview(name = "Large text map summary", fontScale = 1.5f)
@Composable
private fun EarthquakeMapSummaryPreview() {
    SGPreview {
        EarthquakeMapSummary(
            earthquake = EarthquakeListItem(
                "preview", "24 km northeast of a long earthquake location name",
                "4.80", MagnitudeThreshold.FOUR_PLUS, "03.10.2026 09:05",
            ),
            onDismiss = {},
            onViewDetails = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
