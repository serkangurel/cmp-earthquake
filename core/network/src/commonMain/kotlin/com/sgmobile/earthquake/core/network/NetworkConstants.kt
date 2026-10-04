package com.sgmobile.earthquake.core.network

import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
object NetworkConstants {
    const val BASE_URL_USGS = "https://earthquake.usgs.gov/"
}
