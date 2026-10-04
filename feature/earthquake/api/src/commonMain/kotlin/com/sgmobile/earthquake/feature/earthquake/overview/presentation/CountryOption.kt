package com.sgmobile.earthquake.feature.earthquake.overview.presentation

data class CountryOption(
    val code: String,
    val name: String,
    val flag: String,
) {
    companion object {
        /** Code of the worldwide search area. */
        const val GLOBAL_CODE = "GLOBAL"
    }
}
