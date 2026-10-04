package com.sgmobile.earthquake.feature.earthquake.domain

import com.sgmobile.earthquake.feature.earthquake.domain.models.EarthquakeDataset
import kotlinx.coroutines.flow.StateFlow
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

/** Read-only earthquake data and active filters shared between feature implementations. */
@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
interface EarthquakeDatasetSource {
    val state: StateFlow<EarthquakeDataset>
}
