package com.sgmobile.earthquake.feature.earthquake.overview.domain.models

/** How far back earthquake searches look from the current time. */
enum class EarthquakeTimeRange(val days: Int) {
    LAST_DAY(1),
    LAST_WEEK(7),
    LAST_MONTH(30);

    companion object {
        val DEFAULT: EarthquakeTimeRange = LAST_WEEK
    }
}
