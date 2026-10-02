package com.sgmobile.earthquake.feature.earthquake.presentation.components

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.components.formatEarthquakeTimestamp
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.models.EarthquakeTimestamp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale
import java.util.TimeZone

@RunWith(AndroidJUnit4::class)
class EarthquakeTimestampTest {
    @Test
    fun twelve_hour_clock_distinguishes_midnight_and_noon() {
        val midnight = requireNotNull(formatted("02.10.2026 00:05", Locale.US, false))
        val noon = requireNotNull(formatted("02.10.2026 12:05", Locale.US, false))
        assertEquals("Oct 2, 2026", midnight.date)
        assertEquals("12:05 AM", normalizedTime(midnight.time))
        assertEquals("12:05 PM", normalizedTime(noon.time))
    }

    @Test
    fun twenty_four_hour_clock_preserves_time_and_year_boundary() {
        val before = requireNotNull(formatted("31.12.2025 23:59", Locale.US, true))
        val after = requireNotNull(formatted("01.01.2026 00:00", Locale.US, true))
        assertEquals("Dec 31, 2025", before.date)
        assertEquals("23:59", before.time)
        assertEquals("Jan 1, 2026", after.date)
        assertEquals("00:00", after.time)
    }

    @Test
    fun date_uses_the_requested_locale() {
        val timestamp = requireNotNull(formatted("01.10.2026 05:10", Locale.forLanguageTag("tr-TR"), true))
        assertTrue(timestamp.date.contains("Eki"))
        assertTrue(timestamp.date.startsWith("1"))
        assertEquals("05:10", timestamp.time)
    }

    @Test
    fun display_formatting_does_not_apply_a_second_time_zone_conversion() {
        val originalZone = TimeZone.getDefault()
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"))
            val timestamp = requireNotNull(formatted("08.03.2026 02:30", Locale.US, true))
            assertEquals("Mar 8, 2026", timestamp.date)
            assertEquals("02:30", timestamp.time)
        } finally {
            TimeZone.setDefault(originalZone)
        }
    }

    private fun normalizedTime(value: String): String = value.replace('\u202f', ' ').replace('\u00a0', ' ')

    private fun formatted(value: String, locale: Locale, use24Hour: Boolean) =
        formatEarthquakeTimestamp(EarthquakeTimestamp.fromDisplayValue(value), locale, use24Hour)
}
