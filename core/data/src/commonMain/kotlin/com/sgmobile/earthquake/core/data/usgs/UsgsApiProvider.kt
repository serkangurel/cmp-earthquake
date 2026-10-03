package com.sgmobile.earthquake.core.data.usgs

import com.sgmobile.earthquake.core.network.NetworkConstants
import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest
import org.koin.core.annotation.Single

internal class UsgsHttpClient(
    val value: HttpClient,
)

@Single
internal fun provideUsgsHttpClient(
    httpClient: HttpClient,
): UsgsHttpClient =
    UsgsHttpClient(
        httpClient.config {
            defaultRequest {
                url(NetworkConstants.BASE_URL_USGS)
            }
        }
    )

@Single
internal fun provideUsgsApi(
    httpClient: UsgsHttpClient,
): UsgsApi = UsgsApi(httpClient.value)
