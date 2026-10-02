package com.sgmobile.earthquake.feature.earthquake.presentation

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.EarthquakeDateTime
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.models.EarthquakeListItem
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.models.EarthquakeTimestamp
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant

class EarthquakeTimestampTest {
    @Test
    fun preparesCanonicalLocalTimeForNativeFormatting() {
        val value = EarthquakeTimestamp.fromDisplayValue("02.10.2026 09:36")
        assertEquals(epoch("2026-10-02T09:36:00Z"), value.formattingEpochMilliseconds)
        assertNull(value.fallbackText)
    }

    @Test
    fun preservesMidnightNoonAndYearBoundaries() {
        listOf(
            "02.10.2026 00:05" to "2026-10-02T00:05:00Z",
            "02.10.2026 12:05" to "2026-10-02T12:05:00Z",
            "31.12.2025 23:59" to "2025-12-31T23:59:00Z",
            "01.01.2026 00:00" to "2026-01-01T00:00:00Z",
        ).forEach { (raw, expected) ->
            assertEquals(epoch(expected), EarthquakeTimestamp.fromDisplayValue(raw).formattingEpochMilliseconds)
        }
    }

    @Test
    fun validatesCalendarDatesWithoutNormalizingInvalidInput() {
        assertEquals(
            epoch("2024-02-29T12:00:00Z"),
            EarthquakeTimestamp.fromDisplayValue("29.02.2024 12:00").formattingEpochMilliseconds,
        )
        listOf("29.02.2025 12:00", "30.02.2026 12:00", "01.13.2026 12:00", "01.10.2026 25:00", "01.10.2026 05:10 trailing")
            .forEach { raw ->
                val value = EarthquakeTimestamp.fromDisplayValue(raw)
                assertNull(value.formattingEpochMilliseconds)
                assertEquals(raw, value.fallbackText)
            }
    }

    @Test
    fun missingAndUnrecognizedValuesHaveConsistentFallbacks() {
        listOf("", "   ", "\n").forEach { raw ->
            val value = EarthquakeTimestamp.fromDisplayValue(raw)
            assertNull(value.formattingEpochMilliseconds)
            assertNull(value.fallbackText)
        }
        val unknown = EarthquakeTimestamp.fromDisplayValue("unavailable")
        assertNull(unknown.formattingEpochMilliseconds)
        assertEquals("unavailable", unknown.fallbackText)
    }

    @Test
    fun doesNotReinterpretWallTimeDuringDaylightSavingTransitions() {
        assertEquals(
            epoch("2026-03-08T02:30:00Z"),
            EarthquakeTimestamp.fromDisplayValue("08.03.2026 02:30").formattingEpochMilliseconds,
        )
    }

    @Test
    fun listItemsPrepareTimestampsAndCopiesRecomputeThem() {
        val item = EarthquakeListItem("id", "Place", "2.37", MagnitudeThreshold.TWO_PLUS, "02.10.2026 09:36")
        val updated = item.copy(date = "02.10.2026 12:05")
        assertEquals(epoch("2026-10-02T09:36:00Z"), item.timestamp.formattingEpochMilliseconds)
        assertEquals(epoch("2026-10-02T12:05:00Z"), updated.timestamp.formattingEpochMilliseconds)
        assertEquals("02.10.2026 09:36", item.date)
    }

    @Test
    fun codecPreservesTheExistingDisplayContract() {
        val local = LocalDateTime(2026, 10, 2, 9, 36)
        assertEquals("02.10.2026 09:36", EarthquakeDateTime.format(local))
        assertEquals(local, EarthquakeDateTime.parse(EarthquakeDateTime.format(local)))
    }

    private fun epoch(iso: String): Long = Instant.parse(iso).toEpochMilliseconds()
}
