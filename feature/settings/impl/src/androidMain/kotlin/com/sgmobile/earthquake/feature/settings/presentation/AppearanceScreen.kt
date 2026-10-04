package com.sgmobile.earthquake.feature.settings.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sgmobile.earthquake.core.navigation.LocalNavScaffoldPadding
import com.sgmobile.earthquake.core.navigation.LocalNavigator
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.settings_appearance
import com.sgmobile.earthquake.core.resource.settings_theme
import com.sgmobile.earthquake.core.resource.settings_theme_footer
import com.sgmobile.earthquake.core.ui.components.preview.PreviewThemes
import com.sgmobile.earthquake.core.ui.components.preview.SGPreview
import com.sgmobile.earthquake.feature.settings.domain.models.AppTheme
import com.sgmobile.earthquake.feature.settings.presentation.components.SettingsFooter
import com.sgmobile.earthquake.feature.settings.presentation.components.SettingsOptionGroup
import com.sgmobile.earthquake.feature.settings.presentation.components.SettingsScaffold
import com.sgmobile.earthquake.feature.settings.presentation.components.SettingsSectionHeader
import com.sgmobile.earthquake.feature.settings.presentation.extensions.label
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AppearanceScreen(
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navigator = LocalNavigator.current
    AppearanceContent(
        themeOptions = uiState.themeOptions,
        selectedTheme = uiState.preferences.effectiveTheme(isSystemInDarkTheme()),
        bottomPadding = LocalNavScaffoldPadding.current.calculateBottomPadding(),
        onThemeSelect = { viewModel.handleIntent(SettingsIntent.SelectTheme(it)) },
        onBackClick = navigator::goBack,
    )
}

@Composable
internal fun AppearanceContent(
    themeOptions: List<AppTheme>,
    selectedTheme: AppTheme,
    bottomPadding: Dp,
    onThemeSelect: (AppTheme) -> Unit,
    onBackClick: () -> Unit,
) {
    SettingsScaffold(
        title = stringResource(Res.string.settings_appearance),
        bottomPadding = bottomPadding,
        onBackClick = onBackClick,
    ) { contentPadding ->
        LazyColumn(Modifier.fillMaxSize(), contentPadding = contentPadding) {
            item { SettingsSectionHeader(stringResource(Res.string.settings_theme)) }
            item {
                SettingsOptionGroup(
                    options = themeOptions,
                    selected = selectedTheme,
                    label = { stringResource(it.label) },
                    onSelect = onThemeSelect,
                )
            }
            item { SettingsFooter(stringResource(Res.string.settings_theme_footer)) }
        }
    }
}

@PreviewThemes
@Composable
private fun AppearanceContentPreview() {
    SGPreview {
        AppearanceContent(
            themeOptions = AppTheme.entries,
            selectedTheme = AppTheme.LIGHT,
            bottomPadding = 0.dp,
            onThemeSelect = {},
            onBackClick = {},
        )
    }
}
