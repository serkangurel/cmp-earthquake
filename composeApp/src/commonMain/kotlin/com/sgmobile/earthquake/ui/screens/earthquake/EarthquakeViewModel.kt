package com.sgmobile.earthquake.ui.screens.earthquake

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sgmobile.earthquake.data.EarthquakeRepository
import com.sgmobile.earthquake.data.model.CoordinateListItem
import com.sgmobile.earthquake.data.model.EarthquakeRowItemModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EarthquakeViewModel(
    private val earthquakeRepository: EarthquakeRepository
) : ViewModel() {

    var uiState by mutableStateOf(EarthquakeUiState())
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
                    val list = response.features?.take(15)?.map {
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
                        uiModel = EarthquakeUiModel(
                            earhtquakeRowItemList = list
                        )
                    )
                }, onFailure = {
                }
            )
        }
    }
}

data class EarthquakeUiState(
    val isLoading: Boolean = false,
    val uiModel: EarthquakeUiModel? = null
)

data class EarthquakeUiModel(
    val earhtquakeRowItemList: List<EarthquakeRowItemModel>
)