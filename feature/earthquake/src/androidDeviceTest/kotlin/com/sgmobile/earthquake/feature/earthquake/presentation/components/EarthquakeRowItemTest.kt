@file:OptIn(ExperimentalTestApi::class)

package com.sgmobile.earthquake.feature.earthquake.presentation.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sgmobile.earthquake.core.ui.theme.AppTheme
import com.sgmobile.earthquake.feature.earthquake.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.presentation.models.EarthquakeVo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EarthquakeRowItemTest {
    @Test
    fun shows_place_date_and_magnitude_for_two_plus() = runComposeUiTest {
        // Given
        val model = EarthquakeVo(
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
        onNodeWithText("Istanbul, Turkey").assertIsDisplayed()
        onNodeWithText("01.01.2025 12:00").assertIsDisplayed()
        onNodeWithText("3.4").assertIsDisplayed()
    }

    @Test
    fun shows_place_date_and_magnitude_for_five_plus() = runComposeUiTest {
        // Given
        val model = EarthquakeVo(
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
        onNodeWithText("San Francisco").assertIsDisplayed()
        onNodeWithText("19.10.2025 14:30").assertIsDisplayed()
        onNodeWithText("5.2").assertIsDisplayed()
    }
}
