package com.sgmobile.earthquake.data.usgs

import com.sgmobile.earthquake.core.network.NetworkConstants
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.HttpClient
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Named
annotation class KtorfitUsgs

@Single
@KtorfitUsgs
fun provideKtorfitForUsgs(
    httpClient: HttpClient
): Ktorfit = ktorfit {
    baseUrl(NetworkConstants.BASE_URL_USGS)
    httpClient(httpClient)
}

@Single
fun provideUsgsApi(
    @KtorfitUsgs ktorfit: Ktorfit
): UsgsApi = ktorfit.createUsgsApi()