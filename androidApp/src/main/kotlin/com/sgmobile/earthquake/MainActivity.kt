package com.sgmobile.earthquake

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sgmobile.earthquake.feature.settings.domain.SettingsSource
import com.sgmobile.earthquake.feature.settings.domain.models.AppTheme
import com.sgmobile.earthquake.feature.settings.domain.models.SettingsPreferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : ComponentActivity(), KoinComponent {
    private val settingsSource: SettingsSource by inject()
    private var isThemeLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        holdFirstFrameUntilThemeLoads()
        setContent {
            val preferences by settingsSource.preferences
                .collectAsStateWithLifecycle<SettingsPreferences?>(initialValue = null)
            val systemDarkTheme = isSystemInDarkTheme()
            val theme = preferences?.effectiveTheme(systemDarkTheme)
            SideEffect { if (theme != null) isThemeLoaded = true }
            App(
                darkTheme = theme?.let { it == AppTheme.DARK } ?: systemDarkTheme,
                dynamicColor = false
            )
        }
    }

    // Stored settings load asynchronously; drawing earlier could flash the device theme.
    private fun holdFirstFrameUntilThemeLoads() {
        val content = findViewById<View>(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    if (!isThemeLoaded) return false
                    content.viewTreeObserver.removeOnPreDrawListener(this)
                    return true
                }
            }
        )
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(
        darkTheme = isSystemInDarkTheme(),
        dynamicColor = true
    )
}