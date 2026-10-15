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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.UiComposable
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberComposeBitmapDescriptor
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.center_map_on_earthquake
import com.sgmobile.earthquake.core.resource.epicenter_map
import com.sgmobile.earthquake.core.ui.components.map.EarthquakePin
import com.sgmobile.earthquake.core.ui.components.map.mapNightStyleJson
import com.sgmobile.earthquake.core.ui.theme.Spacing
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.extensions.toTierColors
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.models.EarthquakeDetail
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

// No preview: GoogleMap hosts a native map view that cannot render in Compose previews.
@Composable
internal fun EarthquakeMap(
    earthquake: EarthquakeDetail,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val epicenter = LatLng(earthquake.latitude, earthquake.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(epicenter, EPICENTER_ZOOM)
    }
    val coroutineScope = rememberCoroutineScope()
    val motionScale = coroutineScope.coroutineContext[MotionDurationScale]?.scaleFactor ?: 1f
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    val animationsEnabled = lifecycleState.isAtLeast(Lifecycle.State.RESUMED) && motionScale > 0f
    val circleColor = earthquake.magnitudeThreshold.toTierColors().fill
    val isDark = isSystemInDarkTheme()
    val properties = remember(isDark) {
        MapProperties(
            mapStyleOptions = if (isDark) MapStyleOptions(mapNightStyleJson()) else null,
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

    // Re-center the camera target in the padded area once the detail sheet is measured.
    var appliedPadding by remember { mutableStateOf(contentPadding) }
    LaunchedEffect(contentPadding) {
        if (contentPadding != appliedPadding) {
            cameraPositionState.move(CameraUpdateFactory.newCameraPosition(cameraPositionState.position))
            appliedPadding = contentPadding
        }
    }

    Box(modifier = modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = properties,
            uiSettings = uiSettings,
            contentPadding = contentPadding,
            // Dark theme comes from the night style; keep the base map light underneath it.
            mapColorScheme = ComposeMapColorScheme.LIGHT,
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
                .padding(Spacing.space16)
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

@OptIn(MapsComposeExperimentalApi::class)
@Composable
@GoogleMapComposable
private fun EpicenterMarker(center: LatLng, place: String, color: Color) {
    val density = LocalDensity.current
    val iconContent: @Composable @UiComposable () -> Unit = { EarthquakePin(color) }
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
