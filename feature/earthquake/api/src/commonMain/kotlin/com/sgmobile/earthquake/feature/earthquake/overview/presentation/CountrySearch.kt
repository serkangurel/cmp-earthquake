package com.sgmobile.earthquake.feature.earthquake.overview.presentation

interface CountrySearch {
    fun filter(countries: List<CountryOption>, query: String): List<CountryOption>
}
