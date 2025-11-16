package com.sgmobile.earthquake.feature.map.presentation

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
import com.sgmobile.earthquake.core.resource.map
import com.sgmobile.earthquake.core.ui.components.topbar.SGAppBar
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun MapScreen(
    viewModel: MapViewModel = koinViewModel<MapViewModel>()
) {
    Scaffold(
        topBar = {
            SGAppBar(
                screenTitle = stringResource(Res.string.map),
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
            Text("Map Screen")
        }
    }
}