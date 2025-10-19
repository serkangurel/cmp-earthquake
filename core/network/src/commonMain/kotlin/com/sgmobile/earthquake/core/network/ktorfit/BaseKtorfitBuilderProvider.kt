package com.sgmobile.earthquake.core.network.ktorfit

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.ktorfitBuilder
import io.ktor.client.HttpClient
import org.koin.core.annotation.Single

@Single
fun provideBaseKtorfitBuilder(
    httpClient: HttpClient,
): Ktorfit.Builder = ktorfitBuilder {
    httpClient(httpClient)
    converterFactories(KtorfitResponseConverterFactory())
}