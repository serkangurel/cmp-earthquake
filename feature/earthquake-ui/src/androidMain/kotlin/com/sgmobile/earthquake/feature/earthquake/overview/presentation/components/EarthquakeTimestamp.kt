package com.sgmobile.earthquake.feature.earthquake.overview.presentation.components

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.earthquake_time_unavailable
import com.sgmobile.earthquake.core.resource.earthquake_timestamp_accessibility
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.models.EarthquakeTimestamp
import org.jetbrains.compose.resources.stringResource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

internal data class EarthquakeTimestampDisplay(val date: String, val time: String)

@Composable
fun EarthquakeTimestampLabel(value: EarthquakeTimestamp) {
    EarthquakeTimestampContent(rememberEarthquakeTimestamp(value), value)
}

internal fun formatEarthquakeTimestamp(
    value: EarthquakeTimestamp,
    locale: Locale,
    use24Hour: Boolean,
): EarthquakeTimestampDisplay? {
    val parsed = Date(value.formattingEpochMilliseconds ?: return null)
    val zone = TimeZone.getTimeZone("UTC")

    fun formatted(skeleton: String): String = SimpleDateFormat(
        DateFormat.getBestDateTimePattern(locale, skeleton), locale,
    ).apply { timeZone = zone }.format(parsed)

    return EarthquakeTimestampDisplay(
        date = formatted("yMMMd"),
        time = formatted(if (use24Hour) "Hm" else "hm"),
    )
}

@Composable
internal fun rememberEarthquakeTimestamp(value: EarthquakeTimestamp): EarthquakeTimestampDisplay? {
    val locale = LocalConfiguration.current.locales[0]
    val use24Hour = DateFormat.is24HourFormat(LocalContext.current)
    return remember(value, locale, use24Hour) { formatEarthquakeTimestamp(value, locale, use24Hour) }
}

@Composable
internal fun earthquakeTimestampDescription(timestamp: EarthquakeTimestampDisplay?, value: EarthquakeTimestamp): String =
    if (timestamp != null) {
        stringResource(Res.string.earthquake_timestamp_accessibility, timestamp.date, timestamp.time)
    } else {
        value.fallbackText ?: stringResource(Res.string.earthquake_time_unavailable)
    }

@Composable
internal fun EarthquakeTimestampContent(timestamp: EarthquakeTimestampDisplay?, value: EarthquakeTimestamp) {
    if (timestamp == null) {
        TimestampPart(Icons.Outlined.Schedule, earthquakeTimestampDescription(null, value))
        return
    }
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        TimestampPart(Icons.Outlined.DateRange, timestamp.date)
        TimestampPart(Icons.Outlined.Schedule, timestamp.time)
    }
}

@Composable
private fun TimestampPart(icon: ImageVector, text: String) {
    val contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = contentColor,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(fontFeatureSettings = "tnum"),
            fontWeight = FontWeight.Medium,
            color = contentColor,
        )
    }
}
