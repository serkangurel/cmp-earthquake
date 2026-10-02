package com.sgmobile.earthquake.feature.earthquake.overview.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.no_earthquakes_found
import com.sgmobile.earthquake.core.resource.no_earthquakes_found_description
import com.sgmobile.earthquake.core.ui.components.preview.PreviewThemes
import com.sgmobile.earthquake.core.ui.components.preview.SGPreview
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EmptyResultsContent(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
    ) {
        Icon(
            imageVector = Icons.Outlined.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
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
