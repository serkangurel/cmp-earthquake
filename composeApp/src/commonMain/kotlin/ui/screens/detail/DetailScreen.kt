package ui.screens.detail

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
fun DetailScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = koinViewModel<DetailViewModel>()
) {
    if (viewModel.uiState.isLoading) {
        SGLoading()
    }
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        viewModel.uiState.uiModel?.let { uiModel ->
            DetailContent(uiModel)
        }
    }
}

@Composable
fun DetailContent(
    uiModel: DetailUiModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(uiModel.earhtquakeRowItemList) { item ->
            EarthquakeRowItem(item)
        }
    }
}