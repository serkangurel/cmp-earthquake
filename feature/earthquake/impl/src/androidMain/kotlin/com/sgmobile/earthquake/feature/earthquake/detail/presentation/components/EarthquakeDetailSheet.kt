package com.sgmobile.earthquake.feature.earthquake.detail.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.date_and_time_local
import com.sgmobile.earthquake.core.resource.depth
import com.sgmobile.earthquake.core.resource.depth_value
import com.sgmobile.earthquake.core.resource.detail_value_unavailable
import com.sgmobile.earthquake.core.resource.earthquake_share_summary
import com.sgmobile.earthquake.core.resource.ic_calendar
import com.sgmobile.earthquake.core.resource.ic_depth
import com.sgmobile.earthquake.core.resource.magnitude
import com.sgmobile.earthquake.core.resource.source
import com.sgmobile.earthquake.core.ui.components.preview.PreviewThemes
import com.sgmobile.earthquake.core.ui.components.preview.SGPreview
import com.sgmobile.earthquake.core.ui.components.timestamp.rememberTimestamp
import com.sgmobile.earthquake.core.ui.components.timestamp.timestampDescription
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.extensions.toTierColor
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.models.EarthquakeDetail
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun earthquakeDetailDate(earthquake: EarthquakeDetail): String {
    val timestamp = rememberTimestamp(earthquake.timestamp.formattingEpochMilliseconds)
    return if (timestamp != null) {
        "${timestamp.date} · ${timestamp.time}"
    } else {
        timestampDescription(null, earthquake.timestamp.fallbackText)
    }
}

@Composable
internal fun earthquakeDetailDepth(earthquake: EarthquakeDetail): String =
    earthquake.depthKm?.let { stringResource(Res.string.depth_value, it) }
        ?: stringResource(Res.string.detail_value_unavailable)

@Composable
internal fun earthquakeShareSummary(earthquake: EarthquakeDetail): String = stringResource(
    Res.string.earthquake_share_summary,
    earthquake.place,
    earthquake.magnitude,
    earthquakeDetailDate(earthquake),
    earthquakeDetailDepth(earthquake),
)

@Composable
internal fun EarthquakeDetailSheet(
    earthquake: EarthquakeDetail,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shadowElevation = 8.dp,
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            if (LocalDensity.current.fontScale >= 1.5f) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    MagnitudeBadge(earthquake)
                    Location(earthquake)
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    MagnitudeBadge(earthquake)
                    Location(earthquake, Modifier.weight(1f))
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest),
            ) {
                DetailRow(
                    icon = painterResource(Res.drawable.ic_calendar),
                    label = stringResource(Res.string.date_and_time_local),
                    value = earthquakeDetailDate(earthquake),
                )
                DetailDivider()
                DetailRow(
                    icon = painterResource(Res.drawable.ic_depth),
                    label = stringResource(Res.string.depth),
                    value = earthquakeDetailDepth(earthquake),
                )
                DetailDivider()
                DetailRow(
                    icon = rememberVectorPainter(Icons.Outlined.Public),
                    label = stringResource(Res.string.source),
                    value = earthquake.source,
                )
            }
        }
    }
}

@Composable
private fun MagnitudeBadge(earthquake: EarthquakeDetail) {
    val color = earthquake.magnitudeThreshold.toTierColor()
    Column(
        modifier = Modifier
            .semantics(mergeDescendants = true) {}
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = stringResource(Res.string.magnitude),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = color,
        )
        Text(
            text = earthquake.magnitude,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 40.sp,
                lineHeight = 44.sp,
                fontFeatureSettings = "tnum",
            ),
            fontWeight = FontWeight.Bold,
            color = color,
        )
    }
}

@Composable
private fun Location(earthquake: EarthquakeDetail, modifier: Modifier = Modifier) {
    Text(
        text = earthquake.place,
        modifier = modifier.semantics { heading() },
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun DetailRow(icon: Painter, label: String, value: String) {
    val labelStyle = MaterialTheme.typography.bodyMedium
    val valueStyle = labelStyle.copy(fontWeight = FontWeight.SemiBold, fontFeatureSettings = "tnum")
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .semantics(mergeDescendants = true) {}
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        val requiredWidth = textMeasurer.measure(label, labelStyle, maxLines = 1).size.width +
            textMeasurer.measure(value, valueStyle, maxLines = 1).size.width +
            with(density) { 48.dp.roundToPx() }
        if (requiredWidth > constraints.maxWidth) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailLabel(icon, label, labelStyle)
                DetailValue(value, valueStyle)
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DetailLabel(icon, label, labelStyle)
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.width(16.dp))
                DetailValue(value, valueStyle, TextAlign.End)
            }
        }
    }
}

@Composable
private fun DetailLabel(icon: Painter, label: String, style: TextStyle) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(text = label, style = style, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun DetailValue(value: String, style: TextStyle, textAlign: TextAlign = TextAlign.Start) {
    Text(text = value, style = style, color = MaterialTheme.colorScheme.onSurface, textAlign = textAlign)
}

@Composable
private fun DetailDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant,
    )
}

@PreviewThemes
@Preview(name = "Detail • Narrow, large text", widthDp = 320, fontScale = 2f)
@Composable
private fun EarthquakeDetailSheetPreview() {
    SGPreview {
        EarthquakeDetailSheet(
            earthquake = EarthquakeDetail(
                place = "68 km ENE of Punta Cana, Dominican Republic",
                magnitude = "2.96",
                magnitudeThreshold = MagnitudeThreshold.TWO_PLUS,
                depth = "5",
                date = "02.10.2026 15:07",
                latitude = 18.8971,
                longitude = -67.8431,
            ),
            modifier = Modifier.heightIn(max = 420.dp),
        )
    }
}
