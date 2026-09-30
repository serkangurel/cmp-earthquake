package com.sgmobile.earthquake.feature.earthquake.overview.presentation

import com.sgmobile.earthquake.feature.earthquake.constants.EarthquakeConstants
import com.sgmobile.earthquake.feature.earthquake.overview.domain.GetCountriesUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.GetEarthquakeFlowUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.GetIsEndReachedFlowUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.LoadNextUsgsEarthquakesUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.RefreshUsgsEarthquakesUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.Country
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.extensions.mapToListItems
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.extensions.toCountryFlagEmoji
import com.sgmobile.earthquake.feature.earthquake.presentation.Observation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

private const val GLOBAL_COUNTRY_CODE = "GLOBAL"

@Factory
class EarthquakeOverviewController internal constructor(
    private val refreshUsgsEarthquakesUseCase: RefreshUsgsEarthquakesUseCase,
    private val loadNextUsgsEarthquakesUseCase: LoadNextUsgsEarthquakesUseCase,
    getEarthquakeFlowUseCase: GetEarthquakeFlowUseCase,
    getIsEndReachedFlowUseCase: GetIsEndReachedFlowUseCase,
    private val getCountriesUseCase: GetCountriesUseCase,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val earthquakeFlow = getEarthquakeFlowUseCase()
    private val isEndReachedFlow = getIsEndReachedFlowUseCase()
    private val _state = MutableStateFlow(EarthquakeOverviewState.INITIAL)
    private var countries: List<Country> = emptyList()
    private var selectedCountry: Country? = null

    internal val state: StateFlow<EarthquakeOverviewState> = _state.asStateFlow()

    val currentState: EarthquakeOverviewState
        get() = state.value

    init {
        scope.launch {
            combine(earthquakeFlow, isEndReachedFlow) { earthquakes, isEndReached ->
                earthquakes.mapToListItems() to isEndReached
            }.collect { (earthquakes, isEndReached) ->
                _state.update {
                    it.copy(
                        earthquakes = earthquakes,
                        isEndReached = isEndReached,
                    )
                }
            }
        }
        scope.launch {
            countries = getCountriesUseCase()
            selectedCountry = countries.firstOrNull { it.code == GLOBAL_COUNTRY_CODE }
            _state.update { state ->
                state.copy(
                    countries = countries.map(Country::toOption),
                    selectedCountry = selectedCountry?.toOption(),
                )
            }
            requestRefresh(isPullToRefresh = false)
        }
    }

    fun observe(observer: (EarthquakeOverviewState) -> Unit): Observation {
        val job = scope.launch {
            state.collect(observer)
        }
        return JobObservation(job)
    }

    fun refresh() {
        refresh(completion = {})
    }

    fun refresh(completion: () -> Unit) {
        requestRefresh(isPullToRefresh = true, completion = completion)
    }

    fun loadMore() {
        if (state.value.isEndReached || state.value.isLoading) return
        executeWithLoading(isPullToRefresh = false) {
            loadNextUsgsEarthquakesUseCase()
        }
    }

    fun selectMagnitude(magnitude: MagnitudeThreshold) {
        if (magnitude == state.value.selectedMagnitude) return
        _state.update { it.copy(selectedMagnitude = magnitude) }
        requestRefresh(isPullToRefresh = false)
    }

    fun selectCountry(countryCode: String) {
        val country = countries.firstOrNull { it.code == countryCode } ?: return
        if (country == selectedCountry) return
        selectedCountry = country
        _state.update { it.copy(selectedCountry = country.toOption()) }
        requestRefresh(isPullToRefresh = false)
    }

    fun close() {
        scope.cancel()
    }

    private fun requestRefresh(
        isPullToRefresh: Boolean,
        completion: () -> Unit = {},
    ) {
        val countryBounds = selectedCountry?.bounds
        if (countryBounds == null) {
            completion()
            return
        }
        executeWithLoading(isPullToRefresh = isPullToRefresh, completion = completion) {
            refreshUsgsEarthquakesUseCase(
                pageSize = EarthquakeConstants.PAGE_SIZE,
                selectedMagnitude = state.value.selectedMagnitude,
                selectedCountryBounds = countryBounds,
            )
        }
    }

    private fun executeWithLoading(
        isPullToRefresh: Boolean,
        completion: () -> Unit = {},
        block: suspend () -> Unit,
    ) {
        updateLoading(isPullToRefresh = isPullToRefresh, value = true)
        scope.launch {
            try {
                block()
            } finally {
                updateLoading(isPullToRefresh = isPullToRefresh, value = false)
                completion()
            }
        }
    }

    private fun updateLoading(isPullToRefresh: Boolean, value: Boolean) {
        _state.update {
            if (isPullToRefresh) {
                it.copy(isPullToRefresh = value)
            } else {
                it.copy(isLoading = value)
            }
        }
    }
}

private fun Country.toOption() = CountryOption(
    code = code,
    name = name,
    flag = code.toCountryFlagEmoji().orEmpty(),
)

private class JobObservation(
    private val job: Job,
) : Observation {
    override fun cancel() {
        job.cancel()
    }
}
