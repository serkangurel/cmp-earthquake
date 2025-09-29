package com.sgmobile.earthquake.di

import com.sgmobile.earthquake.core.network.di.NetworkModule
import org.koin.core.annotation.Module
import org.koin.dsl.KoinConfiguration
import org.koin.ksp.generated.module

@Module(
    includes = [
        NetworkModule::class,
        ContextModule::class,
        AppModule::class,
    ]
)
class AllModules

fun getKoinConfiguration(): KoinConfiguration = KoinConfiguration {
    modules(
        AllModules().module
    )
}