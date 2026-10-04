package com.sgmobile.earthquake.feature.settings.domain

import com.sgmobile.earthquake.feature.settings.domain.models.SettingsPreferences
import kotlinx.coroutines.flow.Flow
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

/** Read-only stored preferences shared with other features. */
@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
interface SettingsSource {
    /** Emits the stored preferences once loaded, then every change. */
    val preferences: Flow<SettingsPreferences>
}
