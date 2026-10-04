package com.sgmobile.earthquake.feature.settings.presentation

import androidx.lifecycle.ViewModel
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.CountryOption
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.CountrySearch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
internal class SettingsViewModel(
    private val controller: SettingsController,
    private val countrySearch: CountrySearch,
) : ViewModel() {
    val uiState = controller.state

    fun handleIntent(intent: SettingsIntent) = controller.onIntent(intent)

    fun filterCountries(countries: List<CountryOption>, query: String): List<CountryOption> =
        countrySearch.filter(countries, query)

    override fun onCleared() {
        controller.close()
    }
}
