package com.sgmobile.earthquake.feature.earthquake.overview.presentation

import com.sgmobile.earthquake.feature.earthquake.constants.EarthquakeConstants
import com.sgmobile.earthquake.feature.earthquake.data.EarthquakeDatasetStore
import com.sgmobile.earthquake.feature.earthquake.domain.models.EarthquakeDataset
import com.sgmobile.earthquake.feature.earthquake.domain.models.EarthquakeDatasetBounds
import com.sgmobile.earthquake.feature.earthquake.domain.models.EarthquakeDatasetCountry
import com.sgmobile.earthquake.feature.earthquake.domain.models.EarthquakeSummary
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
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val GLOBAL_COUNTRY_CODE = "GLOBAL"

internal class EarthquakeOverviewControllerImpl(
    private val refreshUsgsEarthquakesUseCase: RefreshUsgsEarthquakesUseCase,
    private val loadNextUsgsEarthquakesUseCase: LoadNextUsgsEarthquakesUseCase,
    getEarthquakeFlowUseCase: GetEarthquakeFlowUseCase,
    getIsEndReachedFlowUseCase: GetIsEndReachedFlowUseCase,
    private val getCountriesUseCase: GetCountriesUseCase,
    private val datasetStore: EarthquakeDatasetStore = EarthquakeDatasetStore(),
) : EarthquakeOverviewController {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default.limitedParallelism(1))
    private val earthquakeFlow = getEarthquakeFlowUseCase()
    private val isEndReachedFlow = getIsEndReachedFlowUseCase()
    private val _state = MutableStateFlow(EarthquakeOverviewState.INITIAL)
    private var countries: List<Country> = emptyList()
    private var selectedCountry: Country? = null
    private val requestMutex = Mutex()
    private var requestJob: Job? = null
    private var requestGeneration = 0L
    private var isResetting = true

    override val state: StateFlow<EarthquakeOverviewState> = _state.asStateFlow()

    override val currentState: EarthquakeOverviewState
        get() = state.value

    init {
        datasetStore.publish(EarthquakeDataset.INITIAL)
        scope.launch {
            combine(earthquakeFlow, isEndReachedFlow) { _, _ -> Unit }.collect {
                publishDataset()
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

    override fun observe(observer: (EarthquakeOverviewState) -> Unit): Observation {
        val job = scope.launch {
            state.collect(observer)
        }
        return JobObservation(job)
    }

    override fun refresh() {
        refresh(completion = {})
    }

    override fun refresh(completion: () -> Unit) {
        val dispatchJob = scope.launch { requestRefresh(isPullToRefresh = true)?.join() }
        // Also completes when cancellation prevents the dispatch or request body from starting.
        dispatchJob.invokeOnCompletion { completion() }
    }

    override fun onIntent(intent: EarthquakeScreenIntent) {
        when (intent) {
            is EarthquakeScreenIntent.Refresh -> refresh()
            is EarthquakeScreenIntent.LoadMore -> loadMore()
            is EarthquakeScreenIntent.SelectMagnitude -> selectMagnitude(intent.selectedMagnitude)
            is EarthquakeScreenIntent.SelectCountry -> selectCountry(intent.country.code)
        }
    }

    override fun loadMore() {
        scope.launch { requestLoadMore() }
    }

    /** Loads the next page once the last listed earthquake becomes visible. */
    override fun onEarthquakeDisplayed(id: String) {
        scope.launch {
            if (id == state.value.earthquakes.lastOrNull()?.id) requestLoadMore()
        }
    }

    override fun selectMagnitude(magnitude: MagnitudeThreshold) {
        scope.launch {
            if (magnitude == state.value.selectedMagnitude) return@launch
            _state.update { it.copy(selectedMagnitude = magnitude) }
            requestRefresh(isPullToRefresh = false)
        }
    }

    override fun selectCountry(countryCode: String) {
        scope.launch {
            val country = countries.firstOrNull { it.code == countryCode } ?: return@launch
            if (country == selectedCountry) return@launch
            selectedCountry = country
            _state.update { it.copy(selectedCountry = country.toOption()) }
            requestRefresh(isPullToRefresh = false)
        }
    }

    override fun close() {
        scope.cancel()
    }

    private fun requestLoadMore() {
        if (state.value.isEndReached || state.value.isLoading || state.value.isPullToRefresh) return
        executeWithLoading(isPullToRefresh = false, reset = false) {
            loadNextUsgsEarthquakesUseCase()
        }
    }

    private fun requestRefresh(
        isPullToRefresh: Boolean,
    ): Job? {
        val countryBounds = selectedCountry?.bounds ?: return null
        val magnitude = state.value.selectedMagnitude
        return executeWithLoading(isPullToRefresh = isPullToRefresh, reset = true) {
            refreshUsgsEarthquakesUseCase(
                pageSize = EarthquakeConstants.PAGE_SIZE,
                selectedMagnitude = magnitude,
                selectedCountryBounds = countryBounds,
            )
        }
    }

    private fun executeWithLoading(
        isPullToRefresh: Boolean,
        reset: Boolean,
        block: suspend () -> Unit,
    ): Job {
        val generation = ++requestGeneration
        val previousJob = requestJob
        previousJob?.cancel()
        isResetting = reset
        _state.update {
            it.copy(isLoading = !isPullToRefresh, isPullToRefresh = isPullToRefresh)
        }
        publishDataset()
        val job = scope.launch {
            try {
                previousJob?.cancelAndJoin()
                requestMutex.withLock {
                    ensureActive()
                    block()
                    ensureActive()
                }
            } finally {
                if (generation == requestGeneration) {
                    isResetting = false
                    _state.update { it.copy(isLoading = false, isPullToRefresh = false) }
                    publishDataset()
                }
            }
        }
        requestJob = job
        return job
    }

    private fun publishDataset() {
        // Read the latest value instead of replaying an emission from a superseded request.
        val earthquakes = if (isResetting) emptyList() else earthquakeFlow.value
        _state.update {
            it.copy(earthquakes = earthquakes.mapToListItems(), isEndReached = isEndReachedFlow.value)
        }
        datasetStore.publish(
            EarthquakeDataset(
                country = selectedCountry?.let { country ->
                    EarthquakeDatasetCountry(
                        code = country.code,
                        name = country.name,
                        bounds = EarthquakeDatasetBounds(
                            minLatitude = country.bounds.minLatitude,
                            minLongitude = country.bounds.minLongitude,
                            maxLatitude = country.bounds.maxLatitude,
                            maxLongitude = country.bounds.maxLongitude,
                        ),
                    )
                },
                selectedMagnitude = state.value.selectedMagnitude,
                earthquakes = earthquakes.map { earthquake ->
                    EarthquakeSummary(
                        id = earthquake.id,
                        place = earthquake.place,
                        magnitude = earthquake.magnitude,
                        magnitudeThreshold = earthquake.magnitudeThreshold,
                        date = earthquake.date,
                        latitude = earthquake.latitude,
                        longitude = earthquake.longitude,
                    )
                },
                isLoading = state.value.isLoading || state.value.isPullToRefresh,
            ),
        )
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
