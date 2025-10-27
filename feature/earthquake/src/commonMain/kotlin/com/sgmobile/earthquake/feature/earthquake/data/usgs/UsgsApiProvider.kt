package com.sgmobile.earthquake.feature.earthquake.data.usgs

import com.sgmobile.earthquake.core.network.NetworkConstants
import de.jensklingenberg.ktorfit.Ktorfit
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Named
annotation class KtorfitUsgs

@Single
@KtorfitUsgs
fun provideKtorfitForUsgs(
    baseKtorfitBuilder: Ktorfit.Builder
): Ktorfit = baseKtorfitBuilder.apply {
    baseUrl(NetworkConstants.BASE_URL_USGS)
}.build()

@Single
fun provideUsgsApi(
    @KtorfitUsgs ktorfit: Ktorfit
): UsgsApi = ktorfit.createUsgsApi()