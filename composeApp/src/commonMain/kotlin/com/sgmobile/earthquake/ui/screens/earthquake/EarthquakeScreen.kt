package com.sgmobile.earthquake.ui.screens.earthquake

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sgmobile.earthquake.ui.component.EarthquakeRowItem
import com.sgmobile.earthquake.ui.component.SGLoading
import com.sgmobile.earthquake.ui.screens.earthquake.model.EarthquakeRowItemModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EarthquakeScreen(
    modifier: Modifier = Modifier,
    viewModel: EarthquakeViewModel = koinViewModel<EarthquakeViewModel>()
) {
    if (viewModel.uiState.isLoading) {
        SGLoading()
    }
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        viewModel.uiState.uiModel?.let { uiModel ->
            EarthquakeContent(uiModel)
        }
    }
}

@Composable
fun EarthquakeContent(
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
fun EarthquakeContent() {
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
