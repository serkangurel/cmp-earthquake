package com.sgmobile.earthquake.feature.map.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.map_load_earthquakes
import com.sgmobile.earthquake.core.resource.map_no_loaded_earthquakes
import com.sgmobile.earthquake.core.resource.map_no_loaded_earthquakes_description
import com.sgmobile.earthquake.core.ui.components.loading.SGLoading
import com.sgmobile.earthquake.core.ui.components.preview.PreviewThemes
import com.sgmobile.earthquake.core.ui.components.preview.SGPreview
import com.sgmobile.earthquake.core.ui.theme.Radius
import com.sgmobile.earthquake.core.ui.theme.Spacing
import com.sgmobile.earthquake.feature.map.presentation.EarthquakeMapBounds
import com.sgmobile.earthquake.feature.map.presentation.EarthquakeMapCountry
import com.sgmobile.earthquake.feature.map.presentation.EarthquakeMapSnapshot
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EarthquakeMapStatus(snapshot: EarthquakeMapSnapshot, modifier: Modifier = Modifier) {
    if (!snapshot.isLoading && snapshot.country != null && snapshot.pins.isNotEmpty()) return
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(Radius.xl),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 4.dp,
    ) {
        when {
            snapshot.isLoading -> SGLoading(Modifier.size(64.dp))
            snapshot.country == null -> Text(
                stringResource(Res.string.map_load_earthquakes),
                modifier = Modifier.padding(Spacing.space16),
                style = MaterialTheme.typography.bodyMedium,
            )
            else -> Column(
                Modifier.padding(Spacing.space16),
                verticalArrangement = Arrangement.spacedBy(Spacing.space8),
            ) {
                Text(
                    stringResource(Res.string.map_no_loaded_earthquakes),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    stringResource(Res.string.map_no_loaded_earthquakes_description),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@PreviewThemes
@Composable
private fun EarthquakeMapInitialPreview() {
    SGPreview {
        Box(Modifier.padding(Spacing.space16)) {
            EarthquakeMapStatus(EarthquakeMapSnapshot.INITIAL)
        }
    }
}

@PreviewThemes
@Composable
private fun EarthquakeMapEmptyPreview() {
    SGPreview {
        EarthquakeMapStatus(EarthquakeMapSnapshot.INITIAL.copy(
            country = EarthquakeMapCountry("GLOBAL", "Global", EarthquakeMapBounds(-90.0, -180.0, 90.0, 180.0)),
        ))
    }
}

@PreviewThemes
@Composable
private fun EarthquakeMapLoadingPreview() {
    SGPreview { EarthquakeMapStatus(EarthquakeMapSnapshot.INITIAL.copy(isLoading = true)) }
}
