package com.sgmobile.earthquake.di

import com.sgmobile.earthquake.data.EarthquakeRepository
import com.sgmobile.earthquake.data.NetworkConstants
import com.sgmobile.earthquake.data.UsgsApi
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.ktorfit
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun networkModule(): Module = module {
    singleOf(::provideHttpClient)
    singleOf(::provideKtorfitForUsgs) {
        named(NetworkConstants.KTORFIT_USGS)
    }
    single {
        provideUsgsApi(get(named(NetworkConstants.KTORFIT_USGS)))
    }
    singleOf(::EarthquakeRepository)
}

private fun provideHttpClient(): HttpClient = HttpClient {
    install(Logging) {
        level = LogLevel.ALL
        logger = object : Logger {
            override fun log(message: String) {
                Napier.v(tag = "HTTP Client", message = message)
            }
        }
    }
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            encodeDefaults = true
            ignoreUnknownKeys = true
        })
    }
}

private fun provideKtorfitForUsgs(httpClient: HttpClient): Ktorfit = ktorfit {
    baseUrl(NetworkConstants.BASE_URL_USGS)
    httpClient(httpClient)
}

private fun provideUsgsApi(ktorfit: Ktorfit): UsgsApi = ktorfit.create()