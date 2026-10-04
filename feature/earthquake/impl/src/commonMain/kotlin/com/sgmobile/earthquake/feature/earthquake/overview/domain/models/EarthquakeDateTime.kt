package com.sgmobile.earthquake.feature.earthquake.overview.domain.models

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.char

internal object EarthquakeDateTime {
    private val format = LocalDateTime.Format {
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

    fun format(value: LocalDateTime): String = value.format(format)

    fun parse(value: String): LocalDateTime? {
        val parsed = format.parseOrNull(value) ?: return null
        return parsed.takeIf { format(it) == value }
    }
}
