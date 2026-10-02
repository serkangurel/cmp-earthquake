@file:OptIn(ExperimentalTestApi::class)

package com.sgmobile.earthquake.feature.earthquake.presentation.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sgmobile.earthquake.core.ui.theme.AppTheme
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.CountryOption
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.components.EarthquakeFilters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EarthquakeFiltersTest {
    @Test
    fun labeled_filters_open_country_selection_and_close_magnitude_menu_after_selection() = runComposeUiTest {
        var countryRequested = false
        var selectedMagnitude: MagnitudeThreshold? = null
        setContent {
            AppTheme(darkTheme = false, dynamicColor = false) {
                EarthquakeFilters(
                    selectedCountry = CountryOption("GLOBAL", "Global", "🌍"),
                    selectedMagnitude = MagnitudeThreshold.TWO_PLUS,
                    onCountryClick = { countryRequested = true },
                    onMagnitudeClick = { selectedMagnitude = it },
                )
            }
        }
        onNodeWithContentDescription("Country filter: Global").assertHasClickAction().performClick()
        runOnIdle { assertTrue(countryRequested) }
        onNodeWithContentDescription("Magnitude filter: 2+").performClick()
        onNodeWithText("5+").assertIsDisplayed()
        onNodeWithText("4+").performClick()
        onNodeWithText("5+").assertDoesNotExist()
        runOnIdle { assertEquals(MagnitudeThreshold.FOUR_PLUS, selectedMagnitude) }
    }
}
