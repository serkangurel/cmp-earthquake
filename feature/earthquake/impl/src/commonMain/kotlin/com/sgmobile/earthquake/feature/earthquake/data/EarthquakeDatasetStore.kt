package com.sgmobile.earthquake.feature.earthquake.data

import com.sgmobile.earthquake.feature.earthquake.domain.EarthquakeDatasetSource
import com.sgmobile.earthquake.feature.earthquake.domain.models.EarthquakeDataset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single

@Single(binds = [EarthquakeDatasetSource::class])
internal class EarthquakeDatasetStore : EarthquakeDatasetSource {
    private val dataset = MutableStateFlow(EarthquakeDataset.INITIAL)
    override val state = dataset.asStateFlow()

    fun publish(value: EarthquakeDataset) {
        dataset.value = value
    }
}
