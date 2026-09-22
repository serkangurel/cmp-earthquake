package com.sgmobile.earthquake.core.ui.components.topbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mohamedrejeb.calf.ui.ExperimentalCalfUiApi
import com.mohamedrejeb.calf.ui.navigation.AdaptiveTopBar
import com.mohamedrejeb.calf.ui.navigation.UIKitUIBarButtonItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCalfUiApi::class)
@Composable
fun SGAppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    iosTitle: String = "",
    navigationIcon: @Composable () -> Unit = {},
    iosLeadingItems: List<UIKitUIBarButtonItem> = emptyList(),
    iosTrailingItems: List<UIKitUIBarButtonItem> = emptyList(),
    iosTrailingItemTitles: List<String?> = iosTrailingItems.map { it.title },
    actions: @Composable RowScope.() -> Unit = {},
) {
    AdaptiveTopBar(
        modifier = modifier,
        title = title,
        navigationIcon = navigationIcon,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        actions = actions,
        iosTitle = iosTitle,
        iosLeadingItems = iosLeadingItems,
        iosTrailingItems = iosTrailingItems,
    )
    UpdateUIKitBarButtonItemTitles(iosTrailingItemTitles)
}
