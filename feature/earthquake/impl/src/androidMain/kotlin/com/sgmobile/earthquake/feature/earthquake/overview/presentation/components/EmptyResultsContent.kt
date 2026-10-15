package com.sgmobile.earthquake.feature.earthquake.overview.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.earthquakes_load_failed
import com.sgmobile.earthquake.core.resource.earthquakes_load_failed_description
import com.sgmobile.earthquake.core.resource.no_earthquakes_found
import com.sgmobile.earthquake.core.resource.no_earthquakes_found_description
import com.sgmobile.earthquake.core.resource.retry
import com.sgmobile.earthquake.core.resource.try_source
import com.sgmobile.earthquake.core.ui.components.preview.PreviewThemes
import com.sgmobile.earthquake.core.ui.components.preview.SGPreview
import com.sgmobile.earthquake.core.ui.theme.Spacing
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.EarthquakeSource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EmptyResultsContent(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.SearchOff,
    announce: Boolean = false,
    actions: (@Composable ColumnScope.() -> Unit)? = null,
) {
    Column(
        modifier = modifier.padding(Spacing.space32),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.space12, Alignment.CenterVertically),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = if (announce) {
                Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            } else {
                Modifier
            },
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (actions != null) {
            // Sized to the widest action so actions filling the width share one size.
            Column(
                modifier = Modifier
                    .padding(top = Spacing.space12)
                    .width(IntrinsicSize.Max),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.space12),
                content = actions,
            )
        }
    }
}

@PreviewThemes
@Preview(name = "Large text empty results", fontScale = 1.8f)
@Composable
private fun EmptyResultsContentPreview() {
    SGPreview {
        EmptyResultsContent(
            title = stringResource(Res.string.no_earthquakes_found),
            message = stringResource(Res.string.no_earthquakes_found_description),
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@PreviewThemes
@Preview(name = "Large text load error", fontScale = 1.8f)
@Composable
private fun ErrorResultsContentPreview() {
    SGPreview {
        EmptyResultsContent(
            title = stringResource(Res.string.earthquakes_load_failed),
            message = stringResource(Res.string.earthquakes_load_failed_description, EarthquakeSource.EMSC.label),
            modifier = Modifier.fillMaxSize(),
            icon = Icons.Outlined.CloudOff,
            announce = true,
        ) {
            Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(Res.string.retry))
            }
            OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(Res.string.try_source, EarthquakeSource.USGS.label))
            }
        }
    }
}
