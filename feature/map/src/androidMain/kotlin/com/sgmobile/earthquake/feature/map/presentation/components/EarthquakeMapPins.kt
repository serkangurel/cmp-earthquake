package com.sgmobile.earthquake.feature.map.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.sgmobile.earthquake.core.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.map_earthquake_pin
import com.sgmobile.earthquake.core.resource.map_selected_earthquake_pin
import com.sgmobile.earthquake.core.ui.components.earthquake.toTierColor
import com.sgmobile.earthquake.feature.map.presentation.EarthquakeMapPin
import eu.buney.maps.GoogleMapComposable
import eu.buney.maps.LatLng
import eu.buney.maps.Marker
import eu.buney.maps.rememberComposeBitmapDescriptor
import eu.buney.maps.rememberUpdatedMarkerState
import org.jetbrains.compose.resources.stringResource

@Composable
@GoogleMapComposable
internal fun EarthquakeMapPins(
    pins: List<EarthquakeMapPin>,
    selectedEarthquakeId: String?,
    onEarthquakeSelected: (String) -> Unit,
) {
    val icons = MagnitudeThreshold.entries.associateWith { tier ->
        val color = tier.toTierColor()
        listOf(rememberPinIcon(color, false), rememberPinIcon(color, true))
    }
    pins.forEach { pin ->
        key(pin.earthquake.id) {
            val selected = pin.earthquake.id == selectedEarthquakeId
            val description = stringResource(
                if (selected) Res.string.map_selected_earthquake_pin else Res.string.map_earthquake_pin,
                pin.earthquake.place,
                pin.earthquake.magnitude,
            )
            Marker(
                state = rememberUpdatedMarkerState(LatLng(pin.latitude, pin.longitude)),
                // Native map accessibility exposes the title even when it ignores contentDescription.
                title = description,
                contentDescription = description,
                icon = icons.getValue(pin.earthquake.magnitudeThreshold)[if (selected) 1 else 0],
                anchor = Offset(0.5f, 0.5f),
                zIndex = if (selected) 2f else 1f,
                onClick = {
                    onEarthquakeSelected(pin.earthquake.id)
                    true
                },
            )
        }
    }
}

@Composable
private fun rememberPinIcon(color: Color, selected: Boolean): eu.buney.maps.BitmapDescriptor {
    val density = LocalDensity.current.density
    val centerColor = if (color.luminance() > 0.5f) Color.Black else Color.White
    val content: @Composable @UiComposable () -> Unit = {
        Canvas(Modifier.size(48.dp)) {
            if (selected) {
                drawCircle(color.copy(alpha = 0.3f), radius = 23.dp.toPx())
                drawCircle(color, radius = 20.dp.toPx())
            }
            drawCircle(Color.Black.copy(alpha = 0.15f), radius = 17.dp.toPx(),
                center = center + Offset(0f, 1.dp.toPx()))
            drawCircle(Color.White, radius = 16.dp.toPx())
            drawCircle(color, radius = 14.dp.toPx())
            drawCircle(centerColor, radius = 5.dp.toPx())
            drawCircle(color, radius = 2.dp.toPx())
        }
    }
    return rememberComposeBitmapDescriptor(color, selected, density, content = content)
}
