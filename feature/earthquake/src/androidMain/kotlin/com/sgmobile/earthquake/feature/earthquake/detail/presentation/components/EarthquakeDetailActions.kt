package com.sgmobile.earthquake.feature.earthquake.detail.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.back_button
import com.sgmobile.earthquake.core.resource.share
import com.sgmobile.earthquake.core.ui.components.preview.PreviewThemes
import com.sgmobile.earthquake.core.ui.components.preview.SGPreview
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EarthquakeDetailActions(
    onBackClick: () -> Unit,
    onShareClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        MapActionButton(
            onClick = onBackClick,
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(Res.string.back_button),
        )
        if (onShareClick != null) {
            MapActionButton(
                onClick = onShareClick,
                imageVector = Icons.Filled.Share,
                contentDescription = stringResource(Res.string.share),
            )
        }
    }
}

@Composable
private fun MapActionButton(
    onClick: () -> Unit,
    imageVector: ImageVector,
    contentDescription: String,
) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp).shadow(4.dp, CircleShape),
        shape = CircleShape,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            contentColor = MaterialTheme.colorScheme.primary,
        ),
    ) {
        Icon(imageVector = imageVector, contentDescription = contentDescription)
    }
}

@PreviewThemes
@Composable
private fun EarthquakeDetailActionsPreview() {
    SGPreview {
        EarthquakeDetailActions(onBackClick = {}, onShareClick = {})
    }
}

@PreviewThemes
@Composable
private fun EarthquakeDetailActionsWithoutSharePreview() {
    SGPreview {
        EarthquakeDetailActions(onBackClick = {}, onShareClick = null)
    }
}
