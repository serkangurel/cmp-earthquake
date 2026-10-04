package com.sgmobile.earthquake.feature.map.presentation.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.center_map_on_country
import com.sgmobile.earthquake.core.ui.components.map.mapNightStyleJson
import com.sgmobile.earthquake.feature.map.presentation.EarthquakeMapCountry
import com.sgmobile.earthquake.feature.map.presentation.EarthquakeMapState
import eu.buney.maps.CameraPosition
import eu.buney.maps.CameraUpdate
import eu.buney.maps.CameraUpdateFactory
import eu.buney.maps.GoogleMap
import eu.buney.maps.LatLng
import eu.buney.maps.LatLngBounds
import eu.buney.maps.MapProperties
import eu.buney.maps.MapStyleOptions
import eu.buney.maps.MapUiSettings
import eu.buney.maps.rememberCameraPositionState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

private const val MAX_MERCATOR_LATITUDE = 85.05112878
private val WORLD_CAMERA = CameraPosition(target = LatLng(0.0, 0.0), zoom = 1f)

// The native map is verified on the emulator; only its overlays have Compose previews.
@Composable
internal fun EarthquakeOverviewMap(
    state: EarthquakeMapState,
    contentPadding: PaddingValues,
    onEarthquakeSelected: (String) -> Unit,
    onSelectionDismissed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val camera = rememberCameraPositionState { position = WORLD_CAMERA }
    var lastFramedCountryCode by rememberSaveable { mutableStateOf<String?>(null) }
    var mapLoaded by remember { mutableStateOf(false) }
    var mapSize by remember { mutableStateOf(IntSize.Zero) }
    val country = state.snapshot.country
    val density = LocalDensity.current
    val paddingPx = with(density) { 24.dp.roundToPx() }
    val scope = rememberCoroutineScope()
    val motionScale = scope.coroutineContext[MotionDurationScale]?.scaleFactor ?: 1f
    val darkTheme = isSystemInDarkTheme()
    val properties = remember(darkTheme) {
        MapProperties(mapStyleOptions = if (darkTheme) {
            MapStyleOptions.fromJson(mapNightStyleJson())
        } else null)
    }
    val settings = remember {
        MapUiSettings(compassEnabled = false, indoorLevelPickerEnabled = false,
            mapToolbarEnabled = false, myLocationButtonEnabled = false, zoomControlsEnabled = false)
    }
    val usableHeight = mapSize.height - with(density) {
        (contentPadding.calculateTopPadding() + contentPadding.calculateBottomPadding()).roundToPx()
    }
    val ready = mapLoaded && mapSize.width > paddingPx * 2 && usableHeight > paddingPx * 2

    LaunchedEffect(ready, country?.code) {
        if (ready && country != null && country.code != lastFramedCountryCode) {
            camera.move(countryCameraUpdate(country, paddingPx))
            lastFramedCountryCode = country.code
        }
    }

    Box(modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize().onSizeChanged { mapSize = it },
            cameraPositionState = camera,
            properties = properties,
            uiSettings = settings,
            contentPadding = contentPadding,
            onMapLoaded = { mapLoaded = true },
            onMapClick = { onSelectionDismissed() },
        ) {
            EarthquakeMapPins(state.snapshot.pins, state.selectedEarthquakeId, onEarthquakeSelected)
        }
        IconButton(
            onClick = {
                country?.let {
                    scope.launch {
                        val update = countryCameraUpdate(it, paddingPx)
                        if (motionScale > 0f) camera.animate(update, durationMs = 400) else camera.move(update)
                    }
                }
            },
            enabled = ready && country != null,
            modifier = Modifier.align(Alignment.BottomEnd).padding(contentPadding).padding(16.dp)
                .size(48.dp).shadow(4.dp, CircleShape),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                contentColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            Icon(Icons.Filled.CenterFocusStrong, stringResource(Res.string.center_map_on_country))
        }
    }
}

internal fun countryCameraUpdate(country: EarthquakeMapCountry, paddingPx: Int): CameraUpdate {
    val bounds = country.bounds
    val south = bounds.minLatitude.coerceIn(-MAX_MERCATOR_LATITUDE, MAX_MERCATOR_LATITUDE)
    val north = bounds.maxLatitude.coerceIn(-MAX_MERCATOR_LATITUDE, MAX_MERCATOR_LATITUDE)
    val longitudeExtent = bounds.maxLongitude - bounds.minLongitude
    if (country.code == "GLOBAL" || !south.isFinite() || !north.isFinite() || south >= north ||
        !bounds.minLongitude.isFinite() || !bounds.maxLongitude.isFinite() ||
        bounds.minLongitude !in -180.0..180.0 || bounds.maxLongitude !in -180.0..180.0 ||
        longitudeExtent <= 0.0 || longitudeExtent >= 360.0
    ) {
        return CameraUpdateFactory.newCameraPosition(WORLD_CAMERA)
    }
    return CameraUpdateFactory.newLatLngBounds(
        LatLngBounds(LatLng(south, bounds.minLongitude), LatLng(north, bounds.maxLongitude)),
        paddingPx,
    )
}
