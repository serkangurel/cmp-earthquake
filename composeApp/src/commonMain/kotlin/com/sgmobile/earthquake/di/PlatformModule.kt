@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.sgmobile.earthquake.di

import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.scope.Scope

@Module
expect class PlatformModule() {
    @Single
    fun providePlatformHelper(scope: Scope): PlatformHelper
}

interface PlatformHelper {
    fun getName(): String
}