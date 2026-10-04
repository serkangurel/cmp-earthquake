package com.sgmobile.earthquake.feature.earthquake.overview.presentation.extensions

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.Country
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.CountryOption

internal fun Country.toOption() = CountryOption(
    code = code,
    name = name,
    flag = code.toCountryFlagEmoji().orEmpty(),
)
