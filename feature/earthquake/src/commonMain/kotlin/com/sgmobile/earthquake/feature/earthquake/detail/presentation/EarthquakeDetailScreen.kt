package com.sgmobile.earthquake.feature.earthquake.detail.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sgmobile.earthquake.core.navigation.LocalNavController
import com.sgmobile.earthquake.core.ui.components.topbar.SGAppBar
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun EarthquakeDetailScreen(
    viewModel: EarthquakeDetailViewModel = koinViewModel<EarthquakeDetailViewModel>()
) {
    val navController = LocalNavController.current

    Scaffold(
        topBar = {
            SGAppBar(
                screenTitle = "Earthquake Detail",
                onNavigationClick = navController::navigateUp,
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("Earthquake Detail Screen")
        }
    }
}