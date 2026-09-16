package com.sgmobile.earthquake.feature.earthquake.detail.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.date
import com.sgmobile.earthquake.core.resource.depth
import com.sgmobile.earthquake.core.resource.depth_value
import com.sgmobile.earthquake.core.resource.ic_calendar
import com.sgmobile.earthquake.core.resource.ic_depth
import com.sgmobile.earthquake.core.resource.magnitude
import com.sgmobile.earthquake.core.resource.source
import com.sgmobile.earthquake.core.ui.components.preview.PreviewThemes
import com.sgmobile.earthquake.core.ui.components.preview.SGPreview
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.extensions.toTierColor
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.models.EarthquakeDetailVo
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val SOURCE_USGS = "USGS"
private const val HANDLE_ALPHA = 0.4f

@Composable
internal fun EarthquakeDetailSheet(
    earthquake: EarthquakeDetailVo,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(start = 20.dp, end = 20.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(width = 32.dp, height = 4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = HANDLE_ALPHA),
                        shape = RoundedCornerShape(2.dp),
                    ),
            )
            Text(
                text = stringResource(Res.string.magnitude).uppercase(),
                modifier = Modifier.padding(top = 12.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = earthquake.magnitude,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 72.sp,
                    lineHeight = 74.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-2.5).sp,
                ),
                color = earthquake.magnitudeThreshold.toTierColor(),
            )
            Text(
                text = earthquake.place,
                modifier = Modifier.padding(top = 6.dp),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
                DetailRow(
                    icon = painterResource(Res.drawable.ic_calendar),
                    label = stringResource(Res.string.date),
                    value = earthquake.date,
                )
                DetailDivider()
                DetailRow(
                    icon = painterResource(Res.drawable.ic_depth),
                    label = stringResource(Res.string.depth),
                    value = stringResource(Res.string.depth_value, earthquake.depth),
                )
                DetailDivider()
                DetailRow(
                    icon = rememberVectorPainter(Icons.Outlined.Public),
                    label = stringResource(Res.string.source),
                    value = SOURCE_USGS,
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: Painter,
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun DetailDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 48.dp),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant,
    )
}

@PreviewThemes
@Composable
private fun EarthquakeDetailSheetPreview() {
    SGPreview {
        EarthquakeDetailSheet(
            earthquake = EarthquakeDetailVo(
                place = "12 km SW of Yalova, Turkiye",
                magnitude = "5.80",
                magnitudeThreshold = MagnitudeThreshold.FIVE_PLUS,
                depth = "12.4",
                date = "19.10.2025 14:30",
                latitude = 40.7411,
                longitude = 28.5917,
            )
        )
    }
}
