package com.sgmobile.earthquake.core.ui.components.topbar

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sgmobile.earthquake.core.ui.util.SetSystemBarsLightAppearance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SGAppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    val darkTheme = isSystemInDarkTheme()
    SetSystemBarsLightAppearance(
        isAppearanceLightStatusBars = darkTheme,
        isAppearanceLightNavigationBars = !darkTheme,
    )
    TopAppBar(
        modifier = modifier,
        title = title,
        navigationIcon = navigationIcon,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        actions = actions,
    )
}
