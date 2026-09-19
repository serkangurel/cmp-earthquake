package com.sgmobile.earthquake.core.ui.components.preview

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mohamedrejeb.calf.ui.ExperimentalCalfUiApi
import com.mohamedrejeb.calf.ui.navigation.AdaptiveScaffold
import com.sgmobile.earthquake.core.ui.theme.AppTheme

@OptIn(ExperimentalCalfUiApi::class)
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
        AdaptiveScaffold(content = content)
    }
}
