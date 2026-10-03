package com.sgmobile.earthquake.core.presentation

import com.sgmobile.earthquake.core.domain.models.Country
import com.sgmobile.earthquake.core.domain.models.Earthquake
import com.sgmobile.earthquake.core.domain.models.MagnitudeThreshold
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single

/**
 * Earthquakes currently loaded by the earthquake list, shared with other tabs such as the map.
 */
data class EarthquakeFeed(
    val country: Country?,
    val selectedMagnitude: MagnitudeThreshold,
    val earthquakes: List<Earthquake>,
    val isLoading: Boolean,
) {
    companion object {
        val INITIAL = EarthquakeFeed(null, MagnitudeThreshold.TWO_PLUS, emptyList(), false)
    }
}

@Single
class EarthquakeFeedStore {
    private val feed = MutableStateFlow(EarthquakeFeed.INITIAL)
    val state: StateFlow<EarthquakeFeed> = feed.asStateFlow()

    fun publish(value: EarthquakeFeed) {
        feed.value = value
    }
}
