package com.sgmobile.earthquake.feature.earthquake.overview.presentation

fun filterCountries(countries: List<CountryOption>, query: String): List<CountryOption> {
    val trimmedQuery = query.trim()
    if (trimmedQuery.isEmpty()) return countries
    return countries.filter {
        it.name.contains(trimmedQuery, ignoreCase = true) || it.code.contains(trimmedQuery, ignoreCase = true)
    }
}
