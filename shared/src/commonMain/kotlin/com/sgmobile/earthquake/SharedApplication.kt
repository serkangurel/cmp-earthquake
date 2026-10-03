package com.sgmobile.earthquake

import com.sgmobile.earthquake.core.data.di.DataModule
import com.sgmobile.earthquake.core.domain.di.DomainModule
import com.sgmobile.earthquake.core.network.di.NetworkModule
import com.sgmobile.earthquake.core.presentation.di.PresentationModule
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.EarthquakeDetailController
import com.sgmobile.earthquake.feature.earthquake.di.EarthquakeModule
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.EarthquakeOverviewController
import com.sgmobile.earthquake.feature.map.di.MapModule
import com.sgmobile.earthquake.feature.map.presentation.EarthquakeMapController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.aakira.napier.log
import org.koin.core.Koin
import org.koin.core.annotation.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.includes
import org.koin.plugin.module.dsl.koinConfiguration as annotatedKoinConfiguration

private const val COUNTRY_RESOURCE_PROPERTY = "earthquake.countryResource"

@KoinApplication(
    modules = [
        NetworkModule::class,
        DomainModule::class,
        DataModule::class,
        PresentationModule::class,
        EarthquakeModule::class,
        MapModule::class,
    ],
)
internal class SharedKoinApplication

class SharedApplication internal constructor(
    private val koin: Koin,
) {
    fun makeEarthquakeOverviewController(): EarthquakeOverviewController = koin.get()

    fun makeEarthquakeMapController(): EarthquakeMapController = koin.get()

    fun makeEarthquakeDetailController(earthquakeId: String): EarthquakeDetailController =
        koin.get { parametersOf(earthquakeId) }
}

private var runningApplication: SharedApplication? = null

fun startSharedApplication(countryResource: String): SharedApplication {
    runningApplication?.let { return it }

    Napier.base(DebugAntilog())
    log(tag = "Napier") { "Application Started" }

    val koinApplication = startKoin {
        includes(annotatedKoinConfiguration<SharedKoinApplication>())
        properties(mapOf(COUNTRY_RESOURCE_PROPERTY to countryResource))
    }
    return SharedApplication(koinApplication.koin).also {
        runningApplication = it
    }
}

fun mapsApiKey(): String = BuildKonfig.MAPS_API_KEY
