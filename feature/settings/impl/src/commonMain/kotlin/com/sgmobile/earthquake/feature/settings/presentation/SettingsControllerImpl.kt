package com.sgmobile.earthquake.feature.settings.presentation

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.EarthquakeTimeRange
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.CountryCatalog
import com.sgmobile.earthquake.feature.earthquake.presentation.Observation
import com.sgmobile.earthquake.feature.settings.domain.SettingsRepository
import com.sgmobile.earthquake.feature.settings.domain.models.AppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory(binds = [SettingsController::class])
internal class SettingsControllerImpl(
    private val repository: SettingsRepository,
    private val countryCatalog: CountryCatalog,
) : SettingsController {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val mutableState = MutableStateFlow(SettingsState.INITIAL)
    override val state: StateFlow<SettingsState> = mutableState.asStateFlow()

    override val currentState: SettingsState
        get() = state.value

    init {
        scope.launch {
            val countries = countryCatalog.countries()
            mutableState.update { it.copy(countries = countries) }
        }
        scope.launch {
            repository.preferences.collect { preferences ->
                mutableState.update { it.copy(preferences = preferences) }
            }
        }
    }

    override fun observe(observer: (SettingsState) -> Unit): Observation {
        val job = scope.launch { state.collect(observer) }
        return object : Observation {
            override fun cancel() { job.cancel() }
        }
    }

    override fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.SelectTheme -> selectTheme(intent.theme)
            is SettingsIntent.SelectDefaultMagnitude -> selectDefaultMagnitude(intent.magnitude)
            is SettingsIntent.SelectDefaultCountry -> selectDefaultCountry(intent.countryCode)
            is SettingsIntent.SelectTimeRange -> selectTimeRange(intent.timeRange)
        }
    }

    override fun selectTheme(theme: AppTheme) {
        repository.setTheme(theme)
    }

    override fun selectDefaultMagnitude(magnitude: MagnitudeThreshold) {
        repository.setDefaultMagnitude(magnitude)
    }

    override fun selectDefaultCountry(countryCode: String) {
        if (state.value.countries.none { it.code == countryCode }) return
        repository.setDefaultCountryCode(countryCode)
    }

    override fun selectTimeRange(timeRange: EarthquakeTimeRange) {
        repository.setTimeRange(timeRange)
    }

    override fun close() {
        scope.cancel()
    }
}
