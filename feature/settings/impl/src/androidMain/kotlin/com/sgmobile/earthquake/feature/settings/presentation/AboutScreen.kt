package com.sgmobile.earthquake.feature.settings.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sgmobile.earthquake.core.navigation.LocalNavScaffoldPadding
import com.sgmobile.earthquake.core.navigation.LocalNavigator
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.about_data_source
import com.sgmobile.earthquake.core.resource.about_data_source_description
import com.sgmobile.earthquake.core.resource.about_version
import com.sgmobile.earthquake.core.resource.settings_about
import com.sgmobile.earthquake.core.ui.components.preview.PreviewThemes
import com.sgmobile.earthquake.core.ui.components.preview.SGPreview
import com.sgmobile.earthquake.feature.settings.presentation.components.SettingsFooter
import com.sgmobile.earthquake.feature.settings.presentation.components.SettingsInfoRow
import com.sgmobile.earthquake.feature.settings.presentation.components.SettingsLinkRow
import com.sgmobile.earthquake.feature.settings.presentation.components.SettingsScaffold
import com.sgmobile.earthquake.feature.settings.presentation.components.SettingsSectionHeader
import com.sgmobile.earthquake.feature.settings.presentation.components.rememberAppVersion
import com.sgmobile.earthquake.feature.settings.presentation.extensions.title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AboutScreen(
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navigator = LocalNavigator.current
    val uriHandler = LocalUriHandler.current
    AboutContent(
        appVersion = rememberAppVersion(),
        links = uiState.aboutLinks,
        bottomPadding = LocalNavScaffoldPadding.current.calculateBottomPadding(),
        // Opening fails only when no browser is installed; the row then does nothing.
        onLinkClick = { link -> runCatching { uriHandler.openUri(link.url) } },
        onBackClick = navigator::goBack,
    )
}

@Composable
internal fun AboutContent(
    appVersion: String,
    links: List<AboutLink>,
    bottomPadding: Dp,
    onLinkClick: (AboutLink) -> Unit,
    onBackClick: () -> Unit,
) {
    SettingsScaffold(
        title = stringResource(Res.string.settings_about),
        bottomPadding = bottomPadding,
        onBackClick = onBackClick,
    ) { contentPadding ->
        LazyColumn(Modifier.fillMaxSize(), contentPadding = contentPadding) {
            item {
                SettingsInfoRow(
                    title = stringResource(Res.string.about_version),
                    value = appVersion,
                )
            }
            item { SettingsSectionHeader(stringResource(Res.string.about_data_source)) }
            item { SettingsFooter(stringResource(Res.string.about_data_source_description)) }
            items(links) { link ->
                SettingsLinkRow(
                    title = stringResource(link.title),
                    onClick = { onLinkClick(link) },
                )
            }
        }
    }
}

@PreviewThemes
@Preview(name = "Large text about", fontScale = 1.8f)
@Composable
private fun AboutContentPreview() {
    SGPreview {
        AboutContent(
            appVersion = "1.0.0",
            links = AboutLink.entries,
            bottomPadding = 0.dp,
            onLinkClick = {},
            onBackClick = {},
        )
    }
}
