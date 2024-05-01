package ui.screens.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class DetailViewModel : ViewModel(), KoinComponent {
    var uiState by mutableStateOf(DetailUiState())
        private set

    init {
        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = true
            )
            delay(2000L)
            uiState = uiState.copy(
                isLoading = false,
                uiModel = DetailUiModel(
                    itemCount = 50
                )
            )
        }
    }
}

data class DetailUiState(
    val isLoading: Boolean = false,
    val uiModel: DetailUiModel? = null
)

data class DetailUiModel(
    val itemCount: Int
)