package com.sgmobile.earthquake.feature.settings.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sgmobile.earthquake.core.navigation.LocalNavScaffoldPadding
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.settings
import com.sgmobile.earthquake.core.ui.components.topbar.SGAppBar
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel<SettingsViewModel>()
) {
    Scaffold(
        topBar = {
            SGAppBar(
                screenTitle = stringResource(Res.string.settings),
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = LocalNavScaffoldPadding.current.calculateBottomPadding()
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("Settings Screen")
        }
    }
}