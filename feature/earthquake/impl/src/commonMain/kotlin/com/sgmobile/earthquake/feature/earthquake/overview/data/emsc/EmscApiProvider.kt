package com.sgmobile.earthquake.feature.earthquake.overview.data.emsc

import com.sgmobile.earthquake.core.network.NetworkConstants
import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest
import org.koin.core.annotation.Single

internal class EmscHttpClient(
    val value: HttpClient,
)

@Single
internal fun provideEmscHttpClient(
    httpClient: HttpClient,
): EmscHttpClient =
    EmscHttpClient(
        httpClient.config {
            defaultRequest {
                url(NetworkConstants.BASE_URL_EMSC)
            }
        }
    )

@Single
internal fun provideEmscApi(
    httpClient: EmscHttpClient,
): EmscApi = EmscApi(httpClient.value)
