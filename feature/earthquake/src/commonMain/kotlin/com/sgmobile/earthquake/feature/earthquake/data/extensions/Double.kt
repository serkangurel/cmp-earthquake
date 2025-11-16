package com.sgmobile.earthquake.feature.earthquake.data.extensions

import kotlin.math.round

fun Double.formattedToTwoDecimals(): String {
    val rounded = round(this * 100.0).toLong()
    val intPart = rounded / 100
    val fracPart = rounded % 100

    return buildString {
        append(intPart)
        append('.')
        append(fracPart.toString().padStart(2, '0'))
    }
}