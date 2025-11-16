package com.sgmobile.earthquake.feature.earthquake.domain.models

internal enum class MagnitudeThreshold(
    val value: Double,
    val label: String
) {
    TWO_PLUS(2.0, "2+"),
    FOUR_PLUS(4.0, "4+"),
    FIVE_PLUS(5.0, "5+");

    companion object {
        fun fromLabel(label: String): MagnitudeThreshold =
            entries.firstOrNull { it.label == label } ?: TWO_PLUS

        fun fromValue(value: Double): MagnitudeThreshold =
            entries.firstOrNull { it.value == value } ?: TWO_PLUS

        val labels: List<String> = entries.map { it.label }
        val values: List<Double> = entries.map { it.value }
    }
}