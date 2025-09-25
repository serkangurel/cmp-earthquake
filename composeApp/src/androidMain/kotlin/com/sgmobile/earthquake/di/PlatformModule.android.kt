@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.sgmobile.earthquake.di

import android.content.Context
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.scope.Scope

@Module
actual class PlatformModule {
    @Single
    actual fun providePlatformHelper(scope: Scope): PlatformHelper = PlatformHelperAndroid(scope)
}

class PlatformHelperAndroid(scope: Scope) : PlatformHelper {
    val context: Context = scope.get()
    override fun getName(): String = "I'm Android - ${context.packageName}"
}