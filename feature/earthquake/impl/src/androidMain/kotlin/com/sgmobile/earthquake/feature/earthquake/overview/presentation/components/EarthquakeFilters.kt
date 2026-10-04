package com.sgmobile.earthquake.feature.earthquake.overview.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.country
import com.sgmobile.earthquake.core.resource.country_filter
import com.sgmobile.earthquake.core.resource.magnitude_filter
import com.sgmobile.earthquake.core.resource.minimum_magnitude
import com.sgmobile.earthquake.core.resource.select_country
import com.sgmobile.earthquake.core.ui.components.preview.PreviewThemes
import com.sgmobile.earthquake.core.ui.components.preview.SGPreview
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.CountryOption
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EarthquakeFilters(
    selectedCountry: CountryOption?,
    selectedMagnitude: MagnitudeThreshold,
    magnitudeOptions: List<MagnitudeThreshold>,
    onCountryClick: () -> Unit,
    onMagnitudeClick: (MagnitudeThreshold) -> Unit,
    modifier: Modifier = Modifier,
) {
    val countryName = selectedCountry?.name ?: stringResource(Res.string.select_country)
    val countryDescription = stringResource(Res.string.country_filter, countryName)
    val countryValue = listOfNotNull(selectedCountry?.flag?.takeIf { it.isNotEmpty() }, countryName)
        .joinToString(" ")
    val filtersModifier = modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface)
        .padding(horizontal = 16.dp, vertical = 12.dp)

    if (LocalDensity.current.fontScale >= 1.5f) {
        Column(filtersModifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterButton(
                title = stringResource(Res.string.country),
                value = countryValue,
                description = countryDescription,
                onClick = onCountryClick,
                modifier = Modifier.fillMaxWidth(),
            )
            MagnitudeFilter(selectedMagnitude, magnitudeOptions, onMagnitudeClick, Modifier.fillMaxWidth())
        }
    } else {
        Row(filtersModifier, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterButton(
                title = stringResource(Res.string.country),
                value = countryValue,
                description = countryDescription,
                onClick = onCountryClick,
                modifier = Modifier.weight(1f),
            )
            MagnitudeFilter(selectedMagnitude, magnitudeOptions, onMagnitudeClick, Modifier.weight(1f))
        }
    }
}

@Composable
private fun FilterButton(
    title: String,
    value: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.semantics {
            contentDescription = description
            role = Role.Button
        },
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Column(
            modifier = Modifier
                .heightIn(min = 56.dp)
                .padding(12.dp)
                .clearAndSetSemantics {},
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = value,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun MagnitudeFilter(
    selectedMagnitude: MagnitudeThreshold,
    options: List<MagnitudeThreshold>,
    onSelect: (MagnitudeThreshold) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    BoxWithConstraints(modifier) {
        FilterButton(
            title = stringResource(Res.string.minimum_magnitude),
            value = selectedMagnitude.label,
            description = stringResource(Res.string.magnitude_filter, selectedMagnitude.label),
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(maxWidth),
        ) {
            options.forEach { threshold ->
                val isSelected = threshold == selectedMagnitude
                MagnitudeDropdownItem(
                    threshold = threshold,
                    isSelected = isSelected,
                    onClick = {
                        expanded = false
                        if (!isSelected) onSelect(threshold)
                    },
                )
            }
        }
    }
}

@Composable
private fun MagnitudeDropdownItem(
    threshold: MagnitudeThreshold,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer
    else MaterialTheme.colorScheme.onSurface
    DropdownMenuItem(
        text = {
            Text(
                text = threshold.label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else null,
            )
        },
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clip(MaterialTheme.shapes.small)
            .background(
                if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
            )
            .semantics { selected = isSelected },
        colors = MenuDefaults.itemColors(
            textColor = contentColor,
            trailingIconColor = contentColor,
        ),
        trailingIcon = if (isSelected) {
            { Icon(Icons.Filled.Check, contentDescription = null) }
        } else {
            null
        },
    )
}

@PreviewThemes
@Preview(name = "Large text magnitude menu", fontScale = 1.8f)
@Composable
private fun MagnitudeDropdownItemsPreview() {
    SGPreview {
        Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
            Column(Modifier.width(180.dp).padding(vertical = 8.dp)) {
                MagnitudeThreshold.entries.forEach { threshold ->
                    MagnitudeDropdownItem(
                        threshold = threshold,
                        isSelected = threshold == MagnitudeThreshold.FOUR_PLUS,
                        onClick = {},
                    )
                }
            }
        }
    }
}

@PreviewThemes
@Preview(name = "Large text filters", fontScale = 1.8f)
@Composable
private fun EarthquakeFiltersPreview() {
    SGPreview {
        EarthquakeFilters(
            selectedCountry = CountryOption("AE", "United Arab Emirates", "🇦🇪"),
            selectedMagnitude = MagnitudeThreshold.FOUR_PLUS,
            magnitudeOptions = MagnitudeThreshold.entries,
            onCountryClick = {},
            onMagnitudeClick = {},
        )
    }
}
