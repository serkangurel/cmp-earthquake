package ui.screens.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.EarthquakeRepository
import data.model.CoordinateListItem
import data.model.EarthquakeRowItemModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetailViewModel(
    private val earthquakeRepository: EarthquakeRepository
) : ViewModel() {

    var uiState by mutableStateOf(DetailUiState())
        private set

    init {
        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = true
            )
            delay(500L)
            earthquakeRepository.getUsgsEarthquakes(
                timeInterval = "all_day",
                onSuccess = { response ->
                    val list = response.features?.map {
                        EarthquakeRowItemModel(
                            place = it.properties?.place.orEmpty(),
                            magnitude = it.properties?.mag.toString(),
                            depth = it.geometry?.coordinates?.get(CoordinateListItem.Depth.index).toString(),
                            date = "date",
                            time = "time"
                        )
                    } ?: listOf()
                    uiState = uiState.copy(
                        isLoading = false,
                        uiModel = DetailUiModel(
                            earhtquakeRowItemList = list
                        )
                    )
                }, onFailure = {
                }
            )
        }
    }
}

data class DetailUiState(
    val isLoading: Boolean = false,
    val uiModel: DetailUiModel? = null
)

data class DetailUiModel(
    val earhtquakeRowItemList: List<EarthquakeRowItemModel>
)