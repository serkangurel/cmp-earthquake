package com.sgmobile.earthquake.di

import org.koin.dsl.KoinConfiguration
import org.koin.ksp.generated.module

fun getKoinConfiguration(): KoinConfiguration = KoinConfiguration {
    modules(
        CommonModule().module,
        NetworkModule().module,
        ViewModelModule().module,
    )
}