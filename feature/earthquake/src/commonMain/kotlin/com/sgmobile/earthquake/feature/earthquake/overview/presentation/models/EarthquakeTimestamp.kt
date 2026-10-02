package com.sgmobile.earthquake.feature.earthquake.overview.presentation.models

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.EarthquakeDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

data class EarthquakeTimestamp private constructor(
    // Encodes local wall-time fields in UTC for native formatting only, not event ordering.
    val formattingEpochMilliseconds: Long?,
    val fallbackText: String?,
) {
    companion object {
        fun fromDisplayValue(value: String): EarthquakeTimestamp {
            val local = EarthquakeDateTime.parse(value)
            return EarthquakeTimestamp(
                formattingEpochMilliseconds = local?.toInstant(TimeZone.UTC)?.toEpochMilliseconds(),
                fallbackText = if (local == null) value.takeUnless { it.isBlank() } else null,
            )
        }
    }
}
