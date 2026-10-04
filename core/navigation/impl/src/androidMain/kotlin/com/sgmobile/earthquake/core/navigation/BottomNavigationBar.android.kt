package com.sgmobile.earthquake.core.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.earthquakes
import com.sgmobile.earthquake.core.resource.map
import com.sgmobile.earthquake.core.resource.settings
import com.sgmobile.earthquake.core.ui.components.preview.PreviewThemes
import com.sgmobile.earthquake.core.ui.components.preview.SGPreview
import org.jetbrains.compose.resources.stringResource

internal data class BottomBarItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

@Composable
internal fun BottomNavigationBar(
    items: List<BottomBarItem>,
    selectedIndex: Int,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = NavigationBarDefaults.windowInsets,
) {
    val colorScheme = MaterialTheme.colorScheme
    val labelStyle = MaterialTheme.typography.labelMedium
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    Surface(modifier = modifier, color = colorScheme.surfaceContainerLow) {
        Column {
            HorizontalDivider(color = colorScheme.outlineVariant)
            // Apply insets once, outside measurement, so labels use the actual usable width.
            BoxWithConstraints(Modifier.windowInsetsPadding(windowInsets)) {
                // Leave room for the bar's item gaps and the label's horizontal padding.
                val labelWidth = with(density) {
                    (maxWidth / items.size.coerceAtLeast(1) - 24.dp).roundToPx().coerceAtLeast(1)
                }
                val labelLines = items.maxOfOrNull { item ->
                    textMeasurer.measure(
                        text = item.label,
                        style = labelStyle.copy(fontWeight = FontWeight.SemiBold),
                        constraints = Constraints(maxWidth = labelWidth),
                    ).lineCount
                } ?: 1

                NavigationBar(
                    containerColor = colorScheme.surfaceContainerLow,
                    tonalElevation = 0.dp,
                    windowInsets = WindowInsets(0, 0, 0, 0),
                ) {
                    items.forEachIndexed { index, item ->
                        val isSelected = index == selectedIndex
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { onItemClick(index) },
                            alwaysShowLabel = true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = colorScheme.onPrimaryContainer,
                                selectedTextColor = colorScheme.primary,
                                indicatorColor = colorScheme.primaryContainer,
                                unselectedIconColor = colorScheme.onSurfaceVariant,
                                unselectedTextColor = colorScheme.onSurfaceVariant,
                            ),
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    style = labelStyle,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    // Equal line slots keep icons and labels aligned when any label wraps.
                                    minLines = labelLines,
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@PreviewThemes
@Composable
private fun EarthquakesBottomBarPreview() {
    BottomBarPreview(selectedIndex = 0)
}

@PreviewThemes
@Composable
private fun MapBottomBarPreview() {
    BottomBarPreview(selectedIndex = 1)
}

@PreviewThemes
@Composable
private fun SettingsBottomBarPreview() {
    BottomBarPreview(selectedIndex = 2)
}

@Preview(name = "Large text, narrow", widthDp = 320, fontScale = 2f, locale = "tr")
@Composable
private fun LargeTextBottomBarPreview() {
    BottomBarPreview(selectedIndex = 0)
}

@Preview(name = "Dynamic light", apiLevel = 35, wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE)
@Preview(
    name = "Dynamic dark",
    apiLevel = 35,
    uiMode = AndroidUiModes.UI_MODE_NIGHT_YES,
    wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE,
)
@Composable
private fun DynamicBottomBarPreview() {
    BottomBarPreview(selectedIndex = 1, dynamicColor = true)
}

@Composable
private fun BottomBarPreview(selectedIndex: Int, dynamicColor: Boolean = false) {
    SGPreview(darkTheme = isSystemInDarkTheme(), dynamicColor = dynamicColor) {
        BottomNavigationBar(
            items = listOf(
                BottomBarItem(stringResource(Res.string.earthquakes), Icons.Filled.MonitorHeart, Icons.Outlined.MonitorHeart),
                BottomBarItem(stringResource(Res.string.map), Icons.Filled.Map, Icons.Outlined.Map),
                BottomBarItem(stringResource(Res.string.settings), Icons.Filled.Settings, Icons.Outlined.Settings),
            ),
            selectedIndex = selectedIndex,
            onItemClick = {},
        )
    }
}
