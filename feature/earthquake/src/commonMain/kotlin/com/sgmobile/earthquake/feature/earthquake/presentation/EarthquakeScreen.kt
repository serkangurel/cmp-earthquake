package com.sgmobile.earthquake.feature.earthquake.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sgmobile.earthquake.core.navigation.LocalNavScaffoldPadding
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.earthquakes
import com.sgmobile.earthquake.core.ui.components.SGAppBar
import com.sgmobile.earthquake.core.ui.components.SGLoading
import com.sgmobile.earthquake.feature.earthquake.presentation.components.EarthquakeRowItem
import com.sgmobile.earthquake.feature.earthquake.presentation.models.EarthquakeRowItemModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun EarthquakeScreen(
    viewModel: EarthquakeViewModel = koinViewModel<EarthquakeViewModel>()
) {
    Scaffold(
        topBar = {
            SGAppBar(
                screenTitle = stringResource(Res.string.earthquakes),
                canNavigateBack = false,
                navigateUp = {}
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
        ) {
            viewModel.uiState.uiModel?.let { uiModel ->
                EarthquakeContent(uiModel)
            }
            if (viewModel.uiState.isLoading) {
                SGLoading()
            }
        }
    }
}

@Composable
internal fun EarthquakeContent(
    uiModel: EarthquakeUiModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(uiModel.earhtquakeRowItemList) { item ->
            EarthquakeRowItem(item)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EarthquakeContent() {
    EarthquakeContent(
        uiModel = EarthquakeUiModel(
            listOf(
                EarthquakeRowItemModel("1", "1km", "1", "1"),
                EarthquakeRowItemModel("2", "2km", "2", "2"),
                EarthquakeRowItemModel("3", "3km", "3", "3"),
                EarthquakeRowItemModel("4", "4km", "4", "4")
            )
        )
    )
}
