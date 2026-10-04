package com.sgmobile.earthquake.feature.earthquake.overview.presentation.models

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant

data class EarthquakeTimestamp private constructor(
    // Encodes local wall-time fields in UTC for native formatting only, not event ordering.
    val formattingEpochMilliseconds: Long?,
    val fallbackText: String?,
) {
    companion object {
        private val displayFormat = LocalDateTime.Format {
            day()
            char('.')
            monthNumber()
            char('.')
            year()
            char(' ')
            hour()
            char(':')
            minute()
        }

        fun fromDisplayValue(value: String): EarthquakeTimestamp {
            val local = displayFormat.parseOrNull(value)?.takeIf { it.format(displayFormat) == value }
            return EarthquakeTimestamp(
                formattingEpochMilliseconds = local?.toInstant(TimeZone.UTC)?.toEpochMilliseconds(),
                fallbackText = if (local == null) value.takeUnless { it.isBlank() } else null,
            )
        }
    }
}
