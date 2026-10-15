package com.sgmobile.earthquake.core.ui.components.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.MapStyleOptions

// The renderer is chosen once per process, so maps created later can be styled on their first frame.
private var loadedRenderer: MapsInitializer.Renderer? = null

/**
 * Night style for maps on the legacy renderer, which ignores the SDK's dark color scheme. The latest
 * renderer needs Play Store, so devices and emulator images without it stay on the legacy one.
 * Returns null in light theme and on the latest renderer.
 */
@Composable
fun rememberLegacyRendererNightStyle(darkTheme: Boolean): MapStyleOptions? {
    val context = LocalContext.current.applicationContext
    var renderer by remember { mutableStateOf(loadedRenderer) }
    LaunchedEffect(Unit) {
        if (renderer == null) {
            // A null preference only reports the renderer; it does not change which one loads.
            MapsInitializer.initialize(context, null) {
                loadedRenderer = it
                renderer = it
            }
        }
    }
    // Compared against LATEST because the SDK deprecates the LEGACY constant.
    val legacy = renderer != null && renderer != MapsInitializer.Renderer.LATEST
    return remember(darkTheme, legacy) {
        if (darkTheme && legacy) MapStyleOptions(MAP_NIGHT_STYLE_JSON) else null
    }
}

// Google Maps Styling Wizard "Night" preset. Must stay valid JSON: the map ignores a malformed style.
private val MAP_NIGHT_STYLE_JSON = """
[
  { "elementType": "geometry", "stylers": [{ "color": "#242f3e" }] },
  { "elementType": "labels.text.fill", "stylers": [{ "color": "#746855" }] },
  { "elementType": "labels.text.stroke", "stylers": [{ "color": "#242f3e" }] },
  {
    "featureType": "administrative.locality",
    "elementType": "labels.text.fill",
    "stylers": [{ "color": "#d59563" }]
  },
  {
    "featureType": "poi",
    "elementType": "labels.text.fill",
    "stylers": [{ "color": "#d59563" }]
  },
  {
    "featureType": "poi.park",
    "elementType": "geometry",
    "stylers": [{ "color": "#263c3f" }]
  },
  {
    "featureType": "poi.park",
    "elementType": "labels.text.fill",
    "stylers": [{ "color": "#6b9a76" }]
  },
  {
    "featureType": "road",
    "elementType": "geometry",
    "stylers": [{ "color": "#38414e" }]
  },
  {
    "featureType": "road",
    "elementType": "geometry.stroke",
    "stylers": [{ "color": "#212a37" }]
  },
  {
    "featureType": "road",
    "elementType": "labels.text.fill",
    "stylers": [{ "color": "#9ca5b3" }]
  },
  {
    "featureType": "road.highway",
    "elementType": "geometry",
    "stylers": [{ "color": "#746855" }]
  },
  {
    "featureType": "road.highway",
    "elementType": "geometry.stroke",
    "stylers": [{ "color": "#1f2835" }]
  },
  {
    "featureType": "road.highway",
    "elementType": "labels.text.fill",
    "stylers": [{ "color": "#f3d19c" }]
  },
  {
    "featureType": "transit",
    "elementType": "geometry",
    "stylers": [{ "color": "#2f3948" }]
  },
  {
    "featureType": "transit.station",
    "elementType": "labels.text.fill",
    "stylers": [{ "color": "#d59563" }]
  },
  {
    "featureType": "water",
    "elementType": "geometry",
    "stylers": [{ "color": "#17263c" }]
  },
  {
    "featureType": "water",
    "elementType": "labels.text.fill",
    "stylers": [{ "color": "#515c6d" }]
  },
  {
    "featureType": "water",
    "elementType": "labels.text.stroke",
    "stylers": [{ "color": "#17263c" }]
  }
]
""".trimIndent()
