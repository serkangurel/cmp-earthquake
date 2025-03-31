package com.sgmobile.earthquake.di

import org.koin.dsl.KoinConfiguration

fun getKoinConfiguration(): KoinConfiguration = KoinConfiguration {
    modules(
        commonModule(),
        viewModelModule(),
        platformModule(),
        networkModule()
    )
}