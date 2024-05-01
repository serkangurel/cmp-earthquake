package ui.screens.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ui.component.SGLoading

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = viewModel { DetailViewModel() }
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
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(uiModel.itemCount) {
            Text(
                text = "Item$it",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}