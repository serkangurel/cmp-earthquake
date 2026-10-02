package com.sgmobile.earthquake.feature.earthquake.overview.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.clear_search
import com.sgmobile.earthquake.core.resource.done
import com.sgmobile.earthquake.core.resource.no_countries_found
import com.sgmobile.earthquake.core.resource.no_countries_found_description
import com.sgmobile.earthquake.core.resource.search_countries
import com.sgmobile.earthquake.core.resource.select_country
import com.sgmobile.earthquake.core.ui.components.preview.PreviewThemes
import com.sgmobile.earthquake.core.ui.components.preview.SGPreview
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.CountryOption
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CountrySelectionContent(
    countries: List<CountryOption>,
    selectedCountry: CountryOption?,
    onCountryClick: (CountryOption) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchText by rememberSaveable { mutableStateOf("") }
    val query = searchText.trim()
    val filteredCountries = countries.filter {
        it.name.contains(query, ignoreCase = true) || it.code.contains(query, ignoreCase = true)
    }
    val selectedIndex = countries.indexOf(selectedCountry).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(query) {
        listState.scrollToItem(if (query.isEmpty()) selectedIndex else 0)
    }

    Column(modifier.fillMaxWidth().fillMaxHeight(0.9f)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.select_country),
                modifier = Modifier.weight(1f).semantics { heading() },
                style = MaterialTheme.typography.titleLarge,
            )
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.done)) }
        }
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
            label = { Text(stringResource(Res.string.search_countries)) },
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = if (searchText.isNotEmpty()) {
                {
                    IconButton(onClick = { searchText = "" }) {
                        Icon(Icons.Filled.Clear, stringResource(Res.string.clear_search))
                    }
                }
            } else {
                null
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
        )
        if (filteredCountries.isEmpty()) {
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                EmptyResultsContent(
                    title = stringResource(Res.string.no_countries_found),
                    message = stringResource(Res.string.no_countries_found_description),
                )
            }
        } else {
            LazyColumn(Modifier.fillMaxWidth().weight(1f), state = listState) {
                items(filteredCountries, key = { it.code }) { country ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = country == selectedCountry,
                                role = Role.RadioButton,
                                onClick = { onCountryClick(country) },
                            )
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
                        if (country == selectedCountry) {
                            Icon(Icons.Filled.Check, contentDescription = null)
                        }
                    }
                    HorizontalDivider(Modifier.padding(horizontal = 24.dp))
                }
            }
        }
    }
}

@PreviewThemes
@Preview(name = "Large text countries", fontScale = 1.8f)
@Composable
private fun CountrySelectionContentPreview() {
    val global = CountryOption("GLOBAL", "Global", "🌍")
    SGPreview {
        CountrySelectionContent(
            countries = listOf(global, CountryOption("AE", "United Arab Emirates", "🇦🇪")),
            selectedCountry = global,
            onCountryClick = {},
            onDismiss = {},
        )
    }
}
