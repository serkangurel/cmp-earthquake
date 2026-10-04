package com.sgmobile.earthquake.feature.earthquake.overview.data.extensions

import com.sgmobile.earthquake.feature.earthquake.overview.data.country.CountryBoundsResource
import com.sgmobile.earthquake.feature.earthquake.overview.data.country.CountryResource
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.Country
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.CountryBounds

internal fun CountryResource.toDomain(): Country {
    return Country(
        code = code,
        name = name,
        flagUrl = flagUrl,
        bounds = bounds.toDomain()
    )
}

private fun CountryBoundsResource.toDomain(): CountryBounds {
    return CountryBounds(
        minLongitude = minLongitude,
        minLatitude = minLatitude,
        maxLongitude = maxLongitude,
        maxLatitude = maxLatitude
    )
}
