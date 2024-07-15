package ui.screens.earthquake

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import ui.component.EarthquakeRowItem
import ui.component.SGLoading

@OptIn(KoinExperimentalAPI::class)
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