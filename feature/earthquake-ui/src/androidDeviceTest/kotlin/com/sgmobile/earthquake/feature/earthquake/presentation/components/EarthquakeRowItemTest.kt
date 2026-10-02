@file:OptIn(ExperimentalTestApi::class)

package com.sgmobile.earthquake.feature.earthquake.presentation.components

import android.text.format.DateFormat
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sgmobile.earthquake.core.ui.theme.AppTheme
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.components.EarthquakeRowItem
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.components.formatEarthquakeTimestamp
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.models.EarthquakeListItem
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EarthquakeRowItemTest {
    @Test
    fun shows_place_date_and_magnitude_for_two_plus() = runComposeUiTest {
        // Given
        val model = EarthquakeListItem(
            id = "eq-1",
            place = "Istanbul, Turkey",
            magnitude = "3.4",
            magnitudeThreshold = MagnitudeThreshold.TWO_PLUS,
            date = "01.01.2025 12:00"
        )
        // When
        setContent {
            AppTheme(darkTheme = false, dynamicColor = false) {
                EarthquakeRowItem(model)
            }
        }

        // Then
        onNodeWithContentDescription(rowDescription(model))
            .assertIsDisplayed().assertHasClickAction()
    }

    @Test
    fun shows_place_date_and_magnitude_for_five_plus() = runComposeUiTest {
        // Given
        val model = EarthquakeListItem(
            id = "eq-2",
            place = "San Francisco",
            magnitude = "5.2",
            magnitudeThreshold = MagnitudeThreshold.FIVE_PLUS,
            date = "19.10.2025 14:30"
        )
        // When
        setContent {
            AppTheme(darkTheme = false, dynamicColor = false) {
                EarthquakeRowItem(model)
            }
        }
        // Then
        onNodeWithContentDescription(rowDescription(model))
            .assertIsDisplayed().assertHasClickAction()
    }

    @Test
    fun row_click_invokes_detail_callback_once() = runComposeUiTest {
        var clicks = 0
        val model = EarthquakeListItem(
            id = "eq-click",
            place = "Istanbul, Turkey",
            magnitude = "4.3",
            magnitudeThreshold = MagnitudeThreshold.FOUR_PLUS,
            date = "01.01.2025 12:00"
        )
        setContent {
            AppTheme(darkTheme = false, dynamicColor = false) {
                EarthquakeRowItem(model, onClick = { clicks++ })
            }
        }
        onNodeWithContentDescription(rowDescription(model))
            .performClick()
        runOnIdle { assertEquals(1, clicks) }
    }

    @Test
    fun missing_timestamp_is_explained_in_the_row_label() = runComposeUiTest {
        val model = EarthquakeListItem("missing", "Istanbul, Turkey", "3.4", MagnitudeThreshold.TWO_PLUS, "")
        setContent {
            AppTheme(darkTheme = false, dynamicColor = false) { EarthquakeRowItem(model) }
        }
        onNodeWithContentDescription("Istanbul, Turkey. Magnitude 3.4. Date and time unavailable.")
            .assertIsDisplayed().assertHasClickAction()
    }

    private fun rowDescription(model: EarthquakeListItem): String {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val timestamp = requireNotNull(formatEarthquakeTimestamp(
            model.timestamp, context.resources.configuration.locales[0], DateFormat.is24HourFormat(context),
        ))
        return "${model.place}. Magnitude ${model.magnitude}. ${timestamp.date} at ${timestamp.time} local time."
    }
}
