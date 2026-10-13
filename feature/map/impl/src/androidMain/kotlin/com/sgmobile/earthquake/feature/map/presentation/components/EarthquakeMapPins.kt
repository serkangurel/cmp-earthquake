package com.sgmobile.earthquake.feature.map.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.UiComposable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.map_earthquake_pin
import com.sgmobile.earthquake.core.resource.map_selected_earthquake_pin
import com.sgmobile.earthquake.core.ui.components.map.EarthquakePin
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.map.presentation.EarthquakeMapPin
import com.sgmobile.earthquake.feature.map.presentation.extensions.toTierColors
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
        val color = tier.toTierColors().fill
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
    val content: @Composable @UiComposable () -> Unit = { EarthquakePin(color, selected) }
    return rememberComposeBitmapDescriptor(color, selected, density, content = content)
}
