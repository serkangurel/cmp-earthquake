package com.sgmobile.earthquake.core.ui.components.topbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mohamedrejeb.calf.sf.symbols.SFSymbol
import com.mohamedrejeb.calf.ui.ExperimentalCalfUiApi
import com.mohamedrejeb.calf.ui.navigation.AdaptiveTopBar
import com.mohamedrejeb.calf.ui.navigation.UIKitUIBarButtonItem
import com.mohamedrejeb.calf.ui.uikit.UIKitImage

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCalfUiApi::class)
@Composable
fun SGAppBar(
    modifier: Modifier = Modifier,
    screenTitle: String? = null,
    onNavigationClick: (() -> Unit)? = null,
    iosTrailingItems: List<UIKitUIBarButtonItem> = emptyList(),
    iosTrailingItemTitles: List<String?> = iosTrailingItems.map { it.title },
    actions: @Composable RowScope.() -> Unit = {},
) {
    AdaptiveTopBar(
        modifier = modifier,
        title = { screenTitle?.let { Text(it) } },
        navigationIcon = {
            onNavigationClick?.let { onClick ->
                IconButton(onClick = onClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        actions = actions,
        iosTitle = screenTitle.orEmpty(),
        iosLeadingItems = onNavigationClick?.let { onClick ->
            listOf(
                UIKitUIBarButtonItem.image(
                    image = UIKitImage.SystemName(SFSymbol.chevronBackward),
                    onClick = onClick,
                ),
            )
        }.orEmpty(),
        iosTrailingItems = iosTrailingItems,
    )
    UpdateUIKitBarButtonItemTitles(iosTrailingItemTitles)
}
