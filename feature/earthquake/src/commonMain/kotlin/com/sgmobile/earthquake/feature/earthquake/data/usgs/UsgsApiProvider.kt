package com.sgmobile.earthquake.feature.earthquake.data.usgs

import com.sgmobile.earthquake.core.network.NetworkConstants
import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Named
annotation class UsgsHttpClient

@Single
@UsgsHttpClient
fun provideUsgsHttpClient(
    httpClient: HttpClient
): HttpClient = httpClient.config {
    defaultRequest {
        url(NetworkConstants.BASE_URL_USGS)
    }
}

@Single
fun provideUsgsApi(
    @UsgsHttpClient httpClient: HttpClient
): UsgsApi = UsgsApi(httpClient)