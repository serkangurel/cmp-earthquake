package com.sgmobile.earthquake.feature.settings.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mohamedrejeb.calf.ui.ExperimentalCalfUiApi
import com.mohamedrejeb.calf.ui.navigation.AdaptiveScaffold
import com.sgmobile.earthquake.core.navigation.LocalNavScaffoldPadding
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.settings
import com.sgmobile.earthquake.core.ui.components.topbar.SGAppBar
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalCalfUiApi::class)
@Composable
internal fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel<SettingsViewModel>()
) {
    AdaptiveScaffold(
        topBar = {
            val title = stringResource(Res.string.settings)
            SGAppBar(
                title = { Text(title) },
                iosTitle = title,
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
