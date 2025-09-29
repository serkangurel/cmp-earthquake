@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.sgmobile.earthquake.di

import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.scope.Scope

expect class ContextWrapper

@Module
expect class ContextModule() {
    @Single
    fun provideContextWrapper(scope: Scope): ContextWrapper
}

@Single
expect class PlatformHelper(
    contextWrapper: ContextWrapper
) {
    fun getName(): String
}