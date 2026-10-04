package com.sgmobile.earthquake.feature.earthquake.overview.presentation.extensions

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class CountryFlagEmojiTest {

    @Test
    fun globalCodeShouldReturnWorldEmoji() {
        assertEquals("🌍", "GLOBAL".toCountryFlagEmoji())
    }

    @Test
    fun countryCodeShouldReturnFlagEmoji() {
        assertEquals("🇹🇷", "TR".toCountryFlagEmoji())
    }

    @Test
    fun invalidCodeShouldReturnNull() {
        assertNull("INVALID".toCountryFlagEmoji())
    }
}
