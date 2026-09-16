package com.sgmobile.earthquake.feature.earthquake.detail.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.center_map_on_earthquake
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.extensions.toTierColor
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.models.EarthquakeDetailVo
import eu.buney.maps.CameraPosition
import eu.buney.maps.CameraUpdateFactory
import eu.buney.maps.Circle
import eu.buney.maps.GoogleMap
import eu.buney.maps.GoogleMapComposable
import eu.buney.maps.LatLng
import eu.buney.maps.MapProperties
import eu.buney.maps.MapStyleOptions
import eu.buney.maps.MapUiSettings
import eu.buney.maps.Marker
import eu.buney.maps.rememberCameraPositionState
import eu.buney.maps.rememberUpdatedMarkerState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow

private const val EPICENTER_ZOOM = 8f
private const val CIRCLE_FILL_ALPHA = 0.12f
private const val CIRCLE_STROKE_WIDTH = 4f
private const val CIRCLE_WAVE_DURATION_MILLIS = 2_000
private const val CIRCLE_WAVE_MIN_RADIUS_SCREEN_UNITS = 0.0
private const val CIRCLE_WAVE_MAX_RADIUS_SCREEN_UNITS = 40.0
private const val EARTH_CIRCUMFERENCE_METERS = 40_075_016.686
private const val MAP_WORLD_SIZE_AT_ZOOM_ZERO = 256.0
private const val MAX_MERCATOR_LATITUDE = 85.05112878
private val MAP_CONTROL_PADDING: Dp = 16.dp

// No preview: GoogleMap hosts a native map view that cannot render in Compose previews.
@Composable
internal fun EarthquakeMap(
    earthquake: EarthquakeDetailVo,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val epicenter = LatLng(earthquake.latitude, earthquake.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(target = epicenter, zoom = EPICENTER_ZOOM)
    }
    val coroutineScope = rememberCoroutineScope()
    val circleColor = earthquake.magnitudeThreshold.toTierColor()
    val isDark = isSystemInDarkTheme()
    val properties = remember(isDark) {
        MapProperties(
            mapStyleOptions = if (isDark) MapStyleOptions.fromJson(MAP_NIGHT_STYLE_JSON) else null,
        )
    }
    val uiSettings = remember {
        MapUiSettings(
            compassEnabled = false,
            indoorLevelPickerEnabled = false,
            mapToolbarEnabled = false,
            myLocationButtonEnabled = true,
            zoomControlsEnabled = true,
        )
    }

    Box(modifier = modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = properties,
            uiSettings = uiSettings,
            contentPadding = contentPadding,
        ) {
            Marker(
                state = rememberUpdatedMarkerState(position = epicenter),
                title = earthquake.place,
            )
            CircleWave(
                center = epicenter,
                zoom = cameraPositionState.position.zoom,
                circleColor = circleColor,
            )
        }

        SmallFloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(epicenter, EPICENTER_ZOOM),
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(contentPadding)
                .padding(MAP_CONTROL_PADDING),
        ) {
            Icon(
                imageVector = Icons.Filled.CenterFocusStrong,
                contentDescription = stringResource(Res.string.center_map_on_earthquake),
            )
        }
    }
}

@Composable
@GoogleMapComposable
private fun CircleWave(
    center: LatLng,
    zoom: Float,
    circleColor: Color,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "earthquake circle wave")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = CIRCLE_WAVE_DURATION_MILLIS,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "earthquake circle wave progress",
    )
    val alpha = 1f - progress
    val radiusScreenUnits = CIRCLE_WAVE_MIN_RADIUS_SCREEN_UNITS +
        (CIRCLE_WAVE_MAX_RADIUS_SCREEN_UNITS - CIRCLE_WAVE_MIN_RADIUS_SCREEN_UNITS) * progress
    val latitude = center.latitude.coerceIn(-MAX_MERCATOR_LATITUDE, MAX_MERCATOR_LATITUDE)
    val metersPerScreenUnit = EARTH_CIRCUMFERENCE_METERS * cos(latitude * PI / 180.0) /
        (MAP_WORLD_SIZE_AT_ZOOM_ZERO * 2.0.pow(zoom.toDouble()))

    Circle(
        center = center,
        radius = radiusScreenUnits * metersPerScreenUnit,
        fillColor = circleColor.copy(alpha = CIRCLE_FILL_ALPHA * alpha),
        strokeColor = circleColor.copy(alpha = alpha),
        strokeWidth = CIRCLE_STROKE_WIDTH,
    )
}
