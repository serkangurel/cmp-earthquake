package com.sgmobile.earthquake.core.network.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@Module
@Configuration
@ComponentScan("com.sgmobile.earthquake.core.network")
@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
class NetworkModule
