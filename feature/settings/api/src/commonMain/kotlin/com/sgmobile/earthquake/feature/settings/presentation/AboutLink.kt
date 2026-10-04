package com.sgmobile.earthquake.feature.settings.presentation

/** External pages about the earthquake data provider; platforms supply the localized titles. */
enum class AboutLink(val url: String) {
    USGS_EARTHQUAKE_HAZARDS("https://earthquake.usgs.gov/"),
    USGS_COPYRIGHTS_AND_CREDITS("https://www.usgs.gov/information-policies-and-instructions/copyrights-and-credits"),
    USGS_PRIVACY_POLICY("https://www.usgs.gov/privacy-policies"),
}
