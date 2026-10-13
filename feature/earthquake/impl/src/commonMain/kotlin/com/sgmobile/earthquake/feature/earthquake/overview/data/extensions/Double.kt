package com.sgmobile.earthquake.feature.earthquake.overview.data.extensions

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import kotlin.math.round

internal fun Double.formattedToTwoDecimals(): String {
    val rounded = round(this * 100.0).toLong()
    val intPart = rounded / 100
    val fracPart = rounded % 100

    return buildString {
        append(intPart)
        append('.')
        append(fracPart.toString().padStart(2, '0'))
    }
}

internal fun Double.toMagnitudeThreshold(): MagnitudeThreshold = when (this) {
    in 0.0..<4.0 -> MagnitudeThreshold.TWO_PLUS
    in 4.0..<5.0 -> MagnitudeThreshold.FOUR_PLUS
    else -> MagnitudeThreshold.FIVE_PLUS
}

internal fun Double.toDepthText(): String {
    val rounded = round(this * 10.0) / 10.0
    return if (rounded % 1.0 == 0.0) rounded.toInt().toString() else rounded.toString()
}
