package com.sgmobile.earthquake.feature.settings.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sgmobile.earthquake.core.navigation.LocalNavScaffoldPadding
import com.sgmobile.earthquake.core.navigation.LocalNavigator
import com.sgmobile.earthquake.core.navigation.NavigationDestination
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.settings
import com.sgmobile.earthquake.core.resource.settings_about
import com.sgmobile.earthquake.core.resource.settings_appearance
import com.sgmobile.earthquake.core.resource.settings_default_filters
import com.sgmobile.earthquake.core.resource.settings_default_filters_summary
import com.sgmobile.earthquake.core.resource.settings_section_earthquakes
import com.sgmobile.earthquake.core.resource.settings_section_general
import com.sgmobile.earthquake.core.resource.settings_time_range
import com.sgmobile.earthquake.core.ui.components.preview.PreviewThemes
import com.sgmobile.earthquake.core.ui.components.preview.SGPreview
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.EarthquakeTimeRange
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.CountryOption
import com.sgmobile.earthquake.feature.settings.domain.models.AppTheme
import com.sgmobile.earthquake.feature.settings.domain.models.SettingsPreferences
import com.sgmobile.earthquake.feature.settings.navigation.SettingsRoutes
import com.sgmobile.earthquake.feature.settings.presentation.components.SettingsNavigationRow
import com.sgmobile.earthquake.feature.settings.presentation.components.SettingsScaffold
import com.sgmobile.earthquake.feature.settings.presentation.components.SettingsSectionHeader
import com.sgmobile.earthquake.feature.settings.presentation.components.rememberAppVersion
import com.sgmobile.earthquake.feature.settings.presentation.extensions.label
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navigator = LocalNavigator.current
    SettingsOverviewContent(
        state = uiState,
        selectedTheme = uiState.preferences.effectiveTheme(isSystemInDarkTheme()),
        appVersion = rememberAppVersion(),
        bottomPadding = LocalNavScaffoldPadding.current.calculateBottomPadding(),
        onDestinationClick = navigator::navigate,
    )
}

@Composable
internal fun SettingsOverviewContent(
    state: SettingsState,
    selectedTheme: AppTheme,
    appVersion: String,
    bottomPadding: Dp,
    onDestinationClick: (NavigationDestination) -> Unit,
) {
    SettingsScaffold(
        title = stringResource(Res.string.settings),
        bottomPadding = bottomPadding,
    ) { contentPadding ->
        LazyColumn(Modifier.fillMaxSize(), contentPadding = contentPadding) {
            item { SettingsSectionHeader(stringResource(Res.string.settings_section_general)) }
            item {
                SettingsNavigationRow(
                    title = stringResource(Res.string.settings_appearance),
                    value = stringResource(selectedTheme.label),
                    onClick = { onDestinationClick(SettingsRoutes.Appearance) },
                )
            }
            item { SettingsSectionHeader(stringResource(Res.string.settings_section_earthquakes)) }
            item {
                SettingsNavigationRow(
                    title = stringResource(Res.string.settings_default_filters),
                    value = defaultFiltersSummary(state),
                    onClick = { onDestinationClick(SettingsRoutes.DefaultFilters) },
                )
            }
            item {
                SettingsNavigationRow(
                    title = stringResource(Res.string.settings_time_range),
                    value = stringResource(state.preferences.timeRange.label),
                    onClick = { onDestinationClick(SettingsRoutes.TimeRange) },
                )
            }
            item { HorizontalDivider(Modifier.padding(vertical = 8.dp)) }
            item {
                SettingsNavigationRow(
                    title = stringResource(Res.string.settings_about),
                    value = appVersion,
                    onClick = { onDestinationClick(SettingsRoutes.About) },
                )
            }
        }
    }
}

@Composable
private fun defaultFiltersSummary(state: SettingsState): String {
    val magnitude = state.preferences.defaultMagnitude.label
    val country = state.defaultCountry?.name ?: return magnitude
    return stringResource(Res.string.settings_default_filters_summary, magnitude, country)
}

internal val PreviewSettingsState = SettingsState(
    preferences = SettingsPreferences(
        theme = AppTheme.DARK,
        defaultMagnitude = MagnitudeThreshold.FOUR_PLUS,
        defaultCountryCode = "TR",
        timeRange = EarthquakeTimeRange.LAST_MONTH,
    ),
    countries = listOf(
        CountryOption("GLOBAL", "Global", "🌍"),
        CountryOption("TR", "Türkiye", "🇹🇷"),
        CountryOption("AE", "United Arab Emirates", "🇦🇪"),
    ),
)

@PreviewThemes
@Preview(name = "Large text settings", fontScale = 1.8f)
@Composable
private fun SettingsOverviewContentPreview() {
    SGPreview {
        SettingsOverviewContent(
            state = PreviewSettingsState,
            selectedTheme = AppTheme.DARK,
            appVersion = "1.0.0",
            bottomPadding = 0.dp,
            onDestinationClick = {},
        )
    }
}
