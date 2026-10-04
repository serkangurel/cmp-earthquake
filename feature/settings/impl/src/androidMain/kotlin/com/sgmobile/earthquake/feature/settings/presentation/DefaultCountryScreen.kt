package com.sgmobile.earthquake.feature.settings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sgmobile.earthquake.core.navigation.LocalNavScaffoldPadding
import com.sgmobile.earthquake.core.navigation.LocalNavigator
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.clear_search
import com.sgmobile.earthquake.core.resource.no_countries_found
import com.sgmobile.earthquake.core.resource.no_countries_found_description
import com.sgmobile.earthquake.core.resource.search_countries
import com.sgmobile.earthquake.core.resource.select_country
import com.sgmobile.earthquake.core.ui.components.preview.PreviewThemes
import com.sgmobile.earthquake.core.ui.components.preview.SGPreview
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.CountryOption
import com.sgmobile.earthquake.feature.settings.presentation.components.SettingsScaffold
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun DefaultCountryScreen(
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navigator = LocalNavigator.current
    var query by rememberSaveable { mutableStateOf("") }
    val countries = remember(uiState.countries, query) {
        viewModel.filterCountries(uiState.countries, query)
    }
    DefaultCountryContent(
        countries = countries,
        selectedCountryCode = uiState.preferences.defaultCountryCode,
        query = query,
        showsNoResults = countries.isEmpty() && uiState.countries.isNotEmpty(),
        bottomPadding = LocalNavScaffoldPadding.current.calculateBottomPadding(),
        onQueryChange = { query = it },
        onCountrySelect = { country ->
            viewModel.handleIntent(SettingsIntent.SelectDefaultCountry(country.code))
            navigator.goBack()
        },
        onBackClick = navigator::goBack,
    )
}

@Composable
internal fun DefaultCountryContent(
    countries: List<CountryOption>,
    selectedCountryCode: String,
    query: String,
    showsNoResults: Boolean,
    bottomPadding: Dp,
    onQueryChange: (String) -> Unit,
    onCountrySelect: (CountryOption) -> Unit,
    onBackClick: () -> Unit,
) {
    val layoutDirection = LocalLayoutDirection.current
    val keyboard = LocalSoftwareKeyboardController.current
    SettingsScaffold(
        title = stringResource(Res.string.select_country),
        bottomPadding = bottomPadding,
        onBackClick = onBackClick,
    ) { contentPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(
                    start = contentPadding.calculateStartPadding(layoutDirection),
                    top = contentPadding.calculateTopPadding(),
                    end = contentPadding.calculateEndPadding(layoutDirection),
                )
                .imePadding(),
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
                label = { Text(stringResource(Res.string.search_countries)) },
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = if (query.isNotEmpty()) {
                    {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Filled.Clear, stringResource(Res.string.clear_search))
                        }
                    }
                } else {
                    null
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
            )
            if (showsNoResults) {
                NoCountriesFound(Modifier.fillMaxWidth().weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f).selectableGroup(),
                    contentPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding()),
                ) {
                    items(countries, key = { it.code }) { country ->
                        CountryRow(
                            country = country,
                            isSelected = country.code == selectedCountryCode,
                            onClick = { onCountrySelect(country) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CountryRow(
    country: CountryOption,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick)
            .heightIn(min = 56.dp)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = country.flag,
            modifier = Modifier.widthIn(min = 24.dp).clearAndSetSemantics {},
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = country.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
        )
        if (isSelected) {
            Icon(Icons.Filled.Check, contentDescription = null)
        }
    }
}

@Composable
private fun NoCountriesFound(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
    ) {
        Text(
            text = stringResource(Res.string.no_countries_found),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(Res.string.no_countries_found_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@PreviewThemes
@Composable
private fun DefaultCountryContentPreview() {
    SGPreview {
        DefaultCountryContent(
            countries = PreviewSettingsState.countries,
            selectedCountryCode = "TR",
            query = "",
            showsNoResults = false,
            bottomPadding = 0.dp,
            onQueryChange = {},
            onCountrySelect = {},
            onBackClick = {},
        )
    }
}

@PreviewThemes
@Composable
private fun DefaultCountryNoResultsPreview() {
    SGPreview {
        DefaultCountryContent(
            countries = emptyList(),
            selectedCountryCode = "TR",
            query = "Atlantis",
            showsNoResults = true,
            bottomPadding = 0.dp,
            onQueryChange = {},
            onCountrySelect = {},
            onBackClick = {},
        )
    }
}
