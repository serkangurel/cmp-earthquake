package com.sgmobile.earthquake.core.domain.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@Module
@Configuration
@ComponentScan("com.sgmobile.earthquake.core.domain")
@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
class DomainModule
