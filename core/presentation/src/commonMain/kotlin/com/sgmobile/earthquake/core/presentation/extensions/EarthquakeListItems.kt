package com.sgmobile.earthquake.core.presentation.extensions

import com.sgmobile.earthquake.core.domain.models.Earthquake
import com.sgmobile.earthquake.core.presentation.models.EarthquakeListItem

fun List<Earthquake>.mapToListItems(): List<EarthquakeListItem> {
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
