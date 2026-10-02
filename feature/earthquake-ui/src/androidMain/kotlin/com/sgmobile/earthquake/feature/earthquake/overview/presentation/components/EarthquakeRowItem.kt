package com.sgmobile.earthquake.feature.earthquake.overview.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.earthquake_details_hint
import com.sgmobile.earthquake.core.resource.earthquake_row_accessibility
import com.sgmobile.earthquake.core.resource.magnitude_short
import com.sgmobile.earthquake.core.ui.components.preview.PreviewThemes
import com.sgmobile.earthquake.core.ui.components.preview.SGPreview
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.models.EarthquakeListItem
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EarthquakeRowItem(
    model: EarthquakeListItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val timestamp = rememberEarthquakeTimestamp(model.timestamp)
    val description = stringResource(
        Res.string.earthquake_row_accessibility,
        model.place,
        model.magnitude,
        earthquakeTimestampDescription(timestamp, model.timestamp),
    )
    val actionLabel = stringResource(Res.string.earthquake_details_hint)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClickLabel = actionLabel, onClick = onClick)
            .clearAndSetSemantics {
                contentDescription = description
                role = Role.Button
                this.onClick(label = actionLabel) {
                    onClick()
                    true
                }
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (LocalDensity.current.fontScale >= 1.5f) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MagnitudeBadge(model)
                EarthquakeLocationAndTime(model, timestamp)
            }
        } else {
            MagnitudeBadge(model)
            EarthquakeLocationAndTime(model, timestamp, Modifier.weight(1f))
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun EarthquakeLocationAndTime(
    model: EarthquakeListItem,
    timestamp: EarthquakeTimestampDisplay?,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = model.place,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        EarthquakeTimestampContent(timestamp, model.timestamp)
    }
}

@Composable
private fun MagnitudeBadge(model: EarthquakeListItem) {
    val color = when (model.magnitudeThreshold) {
        MagnitudeThreshold.TWO_PLUS -> MaterialTheme.colorScheme.onSurface
        MagnitudeThreshold.FOUR_PLUS -> MaterialTheme.colorScheme.tertiary
        MagnitudeThreshold.FIVE_PLUS -> MaterialTheme.colorScheme.error
    }
    Column(
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), MaterialTheme.shapes.medium)
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .widthIn(min = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = stringResource(Res.string.magnitude_short),
            style = MaterialTheme.typography.labelSmall,
            color = color,
        )
        Text(
            text = model.magnitude,
            style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum"),
            fontWeight = FontWeight.Bold,
            color = color,
        )
    }
}

@PreviewThemes
@Preview(name = "Large text row", fontScale = 1.8f)
@Composable
private fun EarthquakeRowItemPreview() {
    SGPreview {
        Column {
            MagnitudeThreshold.entries.forEach { threshold ->
                EarthquakeRowItem(
                    model = EarthquakeListItem(
                        id = threshold.label,
                        place = "58 km SSW of Whites City, New Mexico",
                        magnitude = threshold.value.toString(),
                        magnitudeThreshold = threshold,
                        date = "01.10.2026 21:53"
                    )
                )
            }
        }
    }
}

@PreviewThemes
@Preview(name = "Narrow timestamp row", widthDp = 320, fontScale = 1.8f)
@Composable
private fun EarthquakeTimestampRowPreview() {
    SGPreview {
        Column {
            listOf("01.10.2026 00:05", "31.12.2025 23:59", "").forEach { date ->
                EarthquakeRowItem(
                    EarthquakeListItem(date, "Whites City, New Mexico", "4.30", MagnitudeThreshold.FOUR_PLUS, date)
                )
            }
        }
    }
}
