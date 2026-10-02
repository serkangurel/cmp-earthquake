package com.sgmobile.earthquake.feature.earthquake.detail.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.UiComposable
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.center_map_on_earthquake
import com.sgmobile.earthquake.core.resource.epicenter_map
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.extensions.toTierColor
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.models.EarthquakeDetail
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
import eu.buney.maps.rememberComposeBitmapDescriptor
import eu.buney.maps.rememberUpdatedMarkerState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

private const val EPICENTER_ZOOM = 8f
private const val RECENTER_DURATION_MILLIS = 400
private const val CIRCLE_FILL_ALPHA = 0.035f
private const val CIRCLE_STROKE_ALPHA = 0.4f
private const val CIRCLE_WAVE_DURATION_MILLIS = 3_000
private const val CIRCLE_WAVE_MIN_RADIUS_SCREEN_UNITS = 14.0
private const val CIRCLE_WAVE_MAX_RADIUS_SCREEN_UNITS = 56.0
private const val EARTH_CIRCUMFERENCE_METERS = 40_075_016.686
private const val MAP_WORLD_SIZE_AT_ZOOM_ZERO = 256.0
private const val MAX_MERCATOR_LATITUDE = 85.05112878
private val MAP_CONTROL_PADDING: Dp = 16.dp

// No preview: GoogleMap hosts a native map view that cannot render in Compose previews.
@Composable
internal fun EarthquakeMap(
    earthquake: EarthquakeDetail,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val epicenter = LatLng(earthquake.latitude, earthquake.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(target = epicenter, zoom = EPICENTER_ZOOM)
    }
    val coroutineScope = rememberCoroutineScope()
    val motionScale = coroutineScope.coroutineContext[MotionDurationScale]?.scaleFactor ?: 1f
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    val animationsEnabled = lifecycleState.isAtLeast(Lifecycle.State.RESUMED) && motionScale > 0f
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
            myLocationButtonEnabled = false,
            zoomControlsEnabled = false,
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
            EpicenterMarker(epicenter, earthquake.place, circleColor)
            if (animationsEnabled) {
                CircleWave(
                    center = epicenter,
                    zoom = cameraPositionState.position.zoom,
                    circleColor = circleColor,
                )
            }
        }

        IconButton(
            onClick = {
                coroutineScope.launch {
                    val update = CameraUpdateFactory.newLatLngZoom(epicenter, EPICENTER_ZOOM)
                    if (motionScale > 0f) {
                        cameraPositionState.animate(update, durationMs = RECENTER_DURATION_MILLIS)
                    } else {
                        cameraPositionState.move(update)
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(contentPadding)
                .padding(MAP_CONTROL_PADDING)
                .size(48.dp)
                .shadow(4.dp, CircleShape),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.96f),
                contentColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = Color.Unspecified,
                disabledContentColor = Color.Unspecified,
            ),
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
    val latitude = center.latitude.coerceIn(-MAX_MERCATOR_LATITUDE, MAX_MERCATOR_LATITUDE)
    val metersPerScreenUnit = EARTH_CIRCUMFERENCE_METERS * cos(latitude * PI / 180.0) /
        (MAP_WORLD_SIZE_AT_ZOOM_ZERO * 2.0.pow(zoom.toDouble()))
    val strokeWidth = with(LocalDensity.current) { 1.5.dp.toPx() }

    // Decorative waves keep a consistent visual size; they do not show an impact radius.
    repeat(2) { index ->
        val phase = (progress + index / 2f) % 1f
        val easedProgress = 1f - (1f - phase).pow(2)
        val opacity = sin(PI * phase).pow(2).toFloat()
        val radiusScreenUnits = CIRCLE_WAVE_MIN_RADIUS_SCREEN_UNITS +
            (CIRCLE_WAVE_MAX_RADIUS_SCREEN_UNITS - CIRCLE_WAVE_MIN_RADIUS_SCREEN_UNITS) * easedProgress
        Circle(
            center = center,
            radius = radiusScreenUnits * metersPerScreenUnit,
            fillColor = circleColor.copy(alpha = CIRCLE_FILL_ALPHA * opacity),
            strokeColor = circleColor.copy(alpha = CIRCLE_STROKE_ALPHA * opacity),
            strokeWidth = strokeWidth,
        )
    }
}

@Composable
@GoogleMapComposable
private fun EpicenterMarker(center: LatLng, place: String, color: Color) {
    val density = LocalDensity.current
    val centerColor = if (color.luminance() > 0.5f) Color.Black else Color.White
    val iconContent: @Composable @UiComposable () -> Unit = {
        Canvas(Modifier.size(44.dp)) {
            drawCircle(
                color = Color.Black.copy(alpha = 0.15f),
                radius = 17.dp.toPx(),
                center = this.center + Offset(0f, 1.dp.toPx()),
            )
            drawCircle(Color.White, radius = 16.dp.toPx())
            drawCircle(color, radius = 14.dp.toPx())
            drawCircle(centerColor, radius = 5.dp.toPx())
            drawCircle(color, radius = 2.dp.toPx())
        }
    }
    val icon = rememberComposeBitmapDescriptor(color, density.density, content = iconContent)
    Marker(
        state = rememberUpdatedMarkerState(position = center),
        title = place,
        contentDescription = stringResource(Res.string.epicenter_map, place),
        icon = icon,
        anchor = Offset(0.5f, 0.5f),
        zIndex = 1f,
    )
}
