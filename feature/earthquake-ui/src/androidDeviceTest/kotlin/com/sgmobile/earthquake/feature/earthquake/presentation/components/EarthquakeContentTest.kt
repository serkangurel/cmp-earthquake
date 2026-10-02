@file:OptIn(ExperimentalTestApi::class)

package com.sgmobile.earthquake.feature.earthquake.presentation.components

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToKey
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sgmobile.earthquake.core.ui.theme.AppTheme
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.CountryOption
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.EarthquakeContent
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.EarthquakeOverviewState
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.EarthquakeScreenIntent
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.models.EarthquakeListItem
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EarthquakeContentTest {
    @Test
    fun large_text_filter_header_does_not_shift_the_pagination_trigger() = runComposeUiTest {
        var loads = 0
        val state = EarthquakeOverviewState.INITIAL.copy(
            selectedCountry = CountryOption("GLOBAL", "Global", "🌍"),
            earthquakes = List(55) { index ->
                EarthquakeListItem("eq-$index", "Location $index", "2.60", MagnitudeThreshold.TWO_PLUS, "01.10.2026 21:53")
            },
        )
        setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 1.8f)) {
                AppTheme(darkTheme = false, dynamicColor = false) {
                    EarthquakeContent(state, { if (it == EarthquakeScreenIntent.LoadMore) loads++ }, {})
                }
            }
        }
        runOnIdle { assertEquals(0, loads) }
        onNode(hasScrollAction()).performScrollToKey("earthquake:eq-54")
        waitForIdle()
        runOnIdle { assertEquals(1, loads) }
    }

    @Test
    fun empty_list_shows_recovery_message_without_requesting_another_page() = runComposeUiTest {
        var loads = 0
        setContent {
            AppTheme(darkTheme = false, dynamicColor = false) {
                EarthquakeContent(EarthquakeOverviewState.INITIAL, { if (it == EarthquakeScreenIntent.LoadMore) loads++ }, {})
            }
        }
        onNodeWithText("No earthquakes found").assertIsDisplayed()
        onNodeWithText("Try changing the country or magnitude filters, or pull to refresh.").assertIsDisplayed()
        runOnIdle { assertEquals(0, loads) }
    }
}
