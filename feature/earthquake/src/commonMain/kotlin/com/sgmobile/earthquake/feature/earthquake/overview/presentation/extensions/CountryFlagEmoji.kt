package com.sgmobile.earthquake.feature.earthquake.overview.presentation.extensions

internal fun String.toCountryFlagEmoji(): String? {
    val countryCode = uppercase()
    if (countryCode.length != 2 || countryCode.any { it !in 'A'..'Z' }) return null

    return buildString {
        countryCode.forEach { letter ->
            append(REGIONAL_INDICATOR_HIGH_SURROGATE)
            append(
                (REGIONAL_INDICATOR_A_LOW_SURROGATE.code + letter.code - 'A'.code).toChar()
            )
        }
    }
}

private const val REGIONAL_INDICATOR_HIGH_SURROGATE = '\uD83C'
private const val REGIONAL_INDICATOR_A_LOW_SURROGATE = '\uDDE6'
