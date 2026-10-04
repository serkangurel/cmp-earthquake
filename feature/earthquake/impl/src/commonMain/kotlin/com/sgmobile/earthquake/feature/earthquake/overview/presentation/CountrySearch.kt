package com.sgmobile.earthquake.feature.earthquake.overview.presentation

import org.koin.core.annotation.Single

internal fun filterCountries(countries: List<CountryOption>, query: String): List<CountryOption> {
    val trimmedQuery = query.trim()
    if (trimmedQuery.isEmpty()) return countries
    return countries.filter {
        it.name.contains(trimmedQuery, ignoreCase = true) || it.code.contains(trimmedQuery, ignoreCase = true)
    }
}

@Single(binds = [CountrySearch::class])
internal class CountrySearchImpl : CountrySearch {
    override fun filter(countries: List<CountryOption>, query: String): List<CountryOption> =
        filterCountries(countries, query)
}
