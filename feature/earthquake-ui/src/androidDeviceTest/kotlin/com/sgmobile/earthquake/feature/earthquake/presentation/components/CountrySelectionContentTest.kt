@file:OptIn(ExperimentalTestApi::class)

package com.sgmobile.earthquake.feature.earthquake.presentation.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.runComposeUiTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sgmobile.earthquake.core.ui.theme.AppTheme
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.CountryOption
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.components.CountrySelectionContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CountrySelectionContentTest {
    private val global = CountryOption("GLOBAL", "Global", "🌍")
    private val japan = CountryOption("JP", "Japan", "🇯🇵")

    @Test
    fun search_matches_trimmed_case_insensitive_country_codes_and_selects_result() = runComposeUiTest {
        var selected: CountryOption? = null
        setContent {
            AppTheme(darkTheme = false, dynamicColor = false) {
                CountrySelectionContent(listOf(global, japan), global, { selected = it }, {})
            }
        }
        onNode(hasSetTextAction()).performTextInput(" jp ")
        onNodeWithText("Global").assertDoesNotExist()
        onNodeWithText("Japan").assertIsDisplayed().performClick()
        runOnIdle { assertEquals(japan, selected) }
    }

    @Test
    fun empty_search_can_be_cleared_to_recover_countries() = runComposeUiTest {
        setContent {
            AppTheme(darkTheme = false, dynamicColor = false) {
                CountrySelectionContent(listOf(global, japan), global, {}, {})
            }
        }
        onNode(hasSetTextAction()).performTextInput("zzzz")
        onNodeWithText("No countries found").assertIsDisplayed()
        onNodeWithContentDescription("Clear search").performClick()
        onNodeWithText("Global").assertIsDisplayed()
        onNodeWithText("Japan").assertIsDisplayed()
        onNode(hasSetTextAction()).performTextReplacement("jApAn")
        onNodeWithText("Japan").assertIsDisplayed()
        onNodeWithText("Global").assertDoesNotExist()
    }

    @Test
    fun done_dismisses_without_selecting_a_country() = runComposeUiTest {
        var dismissed = false
        var selected: CountryOption? = null
        setContent {
            AppTheme(darkTheme = false, dynamicColor = false) {
                CountrySelectionContent(listOf(global, japan), global, { selected = it }, { dismissed = true })
            }
        }
        onNodeWithText("Done").performClick()
        runOnIdle {
            assertTrue(dismissed)
            assertEquals(null, selected)
        }
    }
}
