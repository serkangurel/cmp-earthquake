package com.sgmobile.earthquake.feature.earthquake.map.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single

@Single
internal class EarthquakeMapSnapshotStore {
    private val snapshot = MutableStateFlow(EarthquakeMapSnapshot.INITIAL)
    val state = snapshot.asStateFlow()

    fun publish(value: EarthquakeMapSnapshot) {
        snapshot.value = value
    }
}
