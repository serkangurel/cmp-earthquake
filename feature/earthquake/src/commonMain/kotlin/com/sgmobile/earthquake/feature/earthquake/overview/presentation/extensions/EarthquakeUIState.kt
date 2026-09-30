package com.sgmobile.earthquake.feature.earthquake.overview.presentation.extensions

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.Earthquake
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.models.EarthquakeListItem

internal fun List<Earthquake>.mapToListItems(): List<EarthquakeListItem> {
    return map { item ->
        EarthquakeListItem(
            id = item.id,
            place = item.place,
            magnitude = item.magnitude,
            magnitudeThreshold = item.magnitudeThreshold,
            date = item.date
        )
    }
}
