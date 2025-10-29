package com.sgmobile.earthquake.feature.earthquake.domain

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.koin.core.annotation.Factory
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, FormatStringsInDatetimeFormats::class)
@Factory
internal class GetUsgsEarthquakeStartTimeUseCase {

    private companion object {
        const val DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss"
    }

    operator fun invoke(): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val yesterdayDate = now.date.minus(DatePeriod(days = 1))
        val yesterdayAtCurrentTime = yesterdayDate.atTime(now.time)

        val formatter = LocalDateTime.Format {
            byUnicodePattern(DATE_PATTERN)
        }
        return formatter.format(yesterdayAtCurrentTime)
    }
}