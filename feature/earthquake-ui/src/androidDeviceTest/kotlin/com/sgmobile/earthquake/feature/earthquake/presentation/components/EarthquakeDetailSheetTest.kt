@file:OptIn(ExperimentalTestApi::class)

package com.sgmobile.earthquake.feature.earthquake.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sgmobile.earthquake.core.ui.theme.AppTheme
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.components.EarthquakeDetailSheet
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.components.earthquakeShareSummary
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.models.EarthquakeDetail
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EarthquakeDetailSheetTest {
    @Test
    fun large_text_keeps_every_fact_reachable_in_a_compact_viewport() = runComposeUiTest {
        setContent {
            AppTheme(darkTheme = false, dynamicColor = false) {
                CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, 2f)) {
                    Box(Modifier.width(320.dp).height(420.dp)) {
                        EarthquakeDetailSheet(earthquake, Modifier.testTag("detail-sheet"))
                    }
                }
            }
        }
        val bounds = onNodeWithTag("detail-sheet").getUnclippedBoundsInRoot()
        assertTrue(bounds.bottom - bounds.top <= 420.dp)
        onNodeWithText(earthquake.place).performScrollTo().assertIsDisplayed()
        onNodeWithText("Date & time").performScrollTo().assertIsDisplayed()
        onNodeWithText("5 km").performScrollTo().assertIsDisplayed()
        onNodeWithText("USGS").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun missing_date_and_depth_have_readable_fallbacks() = runComposeUiTest {
        setContent {
            AppTheme(darkTheme = true, dynamicColor = false) {
                Box(Modifier.width(320.dp).height(420.dp)) {
                    EarthquakeDetailSheet(earthquake.copy(date = "", depth = ""))
                }
            }
        }
        onNodeWithText("Date and time unavailable").performScrollTo().assertIsDisplayed()
        onNodeWithText("Unavailable").performScrollTo().assertIsDisplayed()
        onNodeWithText(" km").assertDoesNotExist()
    }

    @Test
    fun share_summary_includes_event_facts_and_explains_missing_values() = runComposeUiTest {
        var summary = ""
        setContent {
            AppTheme(darkTheme = false, dynamicColor = false) {
                summary = earthquakeShareSummary(earthquake.copy(date = "", depth = ""))
                Text(summary)
            }
        }
        runOnIdle {
            assertTrue(summary.contains(earthquake.place))
            assertTrue(summary.contains("Magnitude: 2.96"))
            assertTrue(summary.contains("Date & time: Date and time unavailable"))
            assertTrue(summary.contains("Depth: Unavailable"))
            assertTrue(summary.contains("Source: USGS"))
        }
    }

    companion object {
        // Recorded USGS event pr2026275000, observed on 2026-10-02.
        private val earthquake = EarthquakeDetail(
            place = "68 km ENE of Punta Cana, Dominican Republic",
            magnitude = "2.96",
            magnitudeThreshold = MagnitudeThreshold.TWO_PLUS,
            depth = "5",
            date = "02.10.2026 15:07",
            latitude = 18.8971,
            longitude = -67.8431,
        )
    }
}
