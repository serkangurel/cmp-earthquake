package com.sgmobile.earthquake.feature.settings.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sgmobile.earthquake.core.navigation.LocalNavScaffoldPadding
import com.sgmobile.earthquake.core.navigation.LocalNavigator
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.country
import com.sgmobile.earthquake.core.resource.minimum_magnitude
import com.sgmobile.earthquake.core.resource.select_country
import com.sgmobile.earthquake.core.resource.settings_default_filters
import com.sgmobile.earthquake.core.resource.settings_default_filters_footer
import com.sgmobile.earthquake.core.ui.components.preview.PreviewThemes
import com.sgmobile.earthquake.core.ui.components.preview.SGPreview
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.settings.navigation.SettingsRoutes
import com.sgmobile.earthquake.feature.settings.presentation.components.SettingsFooter
import com.sgmobile.earthquake.feature.settings.presentation.components.SettingsNavigationRow
import com.sgmobile.earthquake.feature.settings.presentation.components.SettingsOptionGroup
import com.sgmobile.earthquake.feature.settings.presentation.components.SettingsScaffold
import com.sgmobile.earthquake.feature.settings.presentation.components.SettingsSectionHeader
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun DefaultFiltersScreen(
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navigator = LocalNavigator.current
    DefaultFiltersContent(
        magnitudeOptions = uiState.magnitudeOptions,
        selectedMagnitude = uiState.preferences.defaultMagnitude,
        countryName = uiState.defaultCountry?.name,
        bottomPadding = LocalNavScaffoldPadding.current.calculateBottomPadding(),
        onMagnitudeSelect = { viewModel.handleIntent(SettingsIntent.SelectDefaultMagnitude(it)) },
        onCountryClick = { navigator.navigate(SettingsRoutes.DefaultCountry) },
        onBackClick = navigator::goBack,
    )
}

@Composable
internal fun DefaultFiltersContent(
    magnitudeOptions: List<MagnitudeThreshold>,
    selectedMagnitude: MagnitudeThreshold,
    countryName: String?,
    bottomPadding: Dp,
    onMagnitudeSelect: (MagnitudeThreshold) -> Unit,
    onCountryClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    SettingsScaffold(
        title = stringResource(Res.string.settings_default_filters),
        bottomPadding = bottomPadding,
        onBackClick = onBackClick,
    ) { contentPadding ->
        LazyColumn(Modifier.fillMaxSize(), contentPadding = contentPadding) {
            item { SettingsSectionHeader(stringResource(Res.string.minimum_magnitude)) }
            item {
                SettingsOptionGroup(
                    options = magnitudeOptions,
                    selected = selectedMagnitude,
                    label = { it.label },
                    onSelect = onMagnitudeSelect,
                )
            }
            item { SettingsSectionHeader(stringResource(Res.string.country)) }
            item {
                SettingsNavigationRow(
                    title = countryName ?: stringResource(Res.string.select_country),
                    value = null,
                    onClick = onCountryClick,
                )
            }
            item { SettingsFooter(stringResource(Res.string.settings_default_filters_footer)) }
        }
    }
}

@PreviewThemes
@Preview(name = "Large text default filters", fontScale = 1.8f)
@Composable
private fun DefaultFiltersContentPreview() {
    SGPreview {
        DefaultFiltersContent(
            magnitudeOptions = MagnitudeThreshold.entries,
            selectedMagnitude = MagnitudeThreshold.FOUR_PLUS,
            countryName = "United Arab Emirates",
            bottomPadding = 0.dp,
            onMagnitudeSelect = {},
            onCountryClick = {},
            onBackClick = {},
        )
    }
}
