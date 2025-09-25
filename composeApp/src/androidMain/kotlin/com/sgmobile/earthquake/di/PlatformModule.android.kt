@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.sgmobile.earthquake.di

import android.content.Context
import org.koin.core.annotation.Module
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single
import org.koin.core.scope.Scope

actual class ContextWrapper(val context: Context)

@Module
actual class ContextModule {
    @Single
    actual fun provideContextWrapper(scope: Scope): ContextWrapper = ContextWrapper(scope.get())
}

@Single
actual class PlatformHelper actual constructor(
    @Provided val contextWrapper: ContextWrapper
) {
    actual fun getName(): String = "I'm Android - : ${contextWrapper.context.packageName}"
}