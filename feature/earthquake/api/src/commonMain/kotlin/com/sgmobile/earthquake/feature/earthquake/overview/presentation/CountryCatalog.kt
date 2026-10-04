package com.sgmobile.earthquake.feature.earthquake.overview.presentation

import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

/** Countries that earthquake searches can be limited to, for features outside earthquake. */
@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
interface CountryCatalog {
    suspend fun countries(): List<CountryOption>
}
