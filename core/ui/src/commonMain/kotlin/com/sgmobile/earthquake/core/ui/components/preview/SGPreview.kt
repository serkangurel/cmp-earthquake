package com.sgmobile.earthquake.core.ui.components.preview

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.sgmobile.earthquake.core.ui.theme.AppTheme

@Composable
fun SGPreview(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable (PaddingValues) -> Unit
) {
    AppTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor
    ) {
        Scaffold(content = content)
    }
}