package com.sgmobile.earthquake.ui.screens.earthquake

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sgmobile.earthquake.data.EarthquakeRepository
import com.sgmobile.earthquake.data.usgs.CoordinateListItem
import com.sgmobile.earthquake.ui.screens.earthquake.model.EarthquakeRowItemModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.koin.android.annotation.KoinViewModel
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@KoinViewModel
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
                timeInterval = "all_day"
            ).onSuccess { response ->
                val list = response.features?.take(15)?.map {

                    val dateInMillis = it.properties?.time ?: 0L
                    val dateInstant = Instant.fromEpochMilliseconds(dateInMillis)
                    val dateLocal = dateInstant.toLocalDateTime(TimeZone.currentSystemDefault())

                    //dd.MM.yyyy HH:mm
                    val dateFormat = LocalDateTime.Format {
                        day()
                        char('.')
                        monthNumber()
                        char('.')
                        year()
                        char(' ')
                        hour()
                        char(':')
                        minute()
                    }
                    val formattedDate = dateLocal.format(dateFormat)

                    EarthquakeRowItemModel(
                        place = it.properties?.place.orEmpty(),
                        magnitude = it.properties?.mag.toString(),
                        depth = it.geometry?.coordinates?.get(CoordinateListItem.Depth.index)
                            .toString(),
                        date = formattedDate
                    )
                } ?: listOf()
                uiState = uiState.copy(
                    isLoading = false,
                    uiModel = EarthquakeUiModel(
                        earhtquakeRowItemList = list
                    )
                )
            }.onFailure {
                uiState = uiState.copy(
                    isLoading = true
                )
            }
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