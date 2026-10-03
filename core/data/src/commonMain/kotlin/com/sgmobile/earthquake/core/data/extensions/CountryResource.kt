package com.sgmobile.earthquake.core.data.extensions

import com.sgmobile.earthquake.core.data.country.CountryBoundsResource
import com.sgmobile.earthquake.core.data.country.CountryResource
import com.sgmobile.earthquake.core.domain.models.Country
import com.sgmobile.earthquake.core.domain.models.CountryBounds

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
