package com.sgmobile.earthquake

import com.sgmobile.earthquake.core.network.di.NetworkModule
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.EarthquakeDetailController
import com.sgmobile.earthquake.feature.earthquake.di.EarthquakeModule
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.CountryOption
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.CountrySearch
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.EarthquakeOverviewController
import com.sgmobile.earthquake.feature.map.di.MapModule
import com.sgmobile.earthquake.feature.map.presentation.EarthquakeMapController
import com.sgmobile.earthquake.feature.settings.di.SettingsModule
import com.sgmobile.earthquake.feature.settings.presentation.SettingsController
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
private const val SETTINGS_PREFERENCES_PATH_PROPERTY = "settings.preferencesPath"
private const val SETTINGS_PREFERENCES_FILE_NAME = "settings.preferences_pb"

@KoinApplication(
    modules = [
        NetworkModule::class,
        EarthquakeModule::class,
        MapModule::class,
        SettingsModule::class,
    ],
)
internal class SharedKoinApplication

class SharedApplication internal constructor(
    private val koin: Koin,
) {
    fun filterCountries(countries: List<CountryOption>, query: String): List<CountryOption> =
        koin.get<CountrySearch>().filter(countries, query)

    fun makeEarthquakeOverviewController(): EarthquakeOverviewController = koin.get()

    fun makeEarthquakeMapController(): EarthquakeMapController = koin.get()

    fun makeEarthquakeDetailController(earthquakeId: String): EarthquakeDetailController =
        koin.get { parametersOf(earthquakeId) }

    fun makeSettingsController(): SettingsController = koin.get()
}

private var runningApplication: SharedApplication? = null

/** [preferencesDirectory] is an existing app-private directory that stores user settings. */
fun startSharedApplication(countryResource: String, preferencesDirectory: String): SharedApplication {
    runningApplication?.let { return it }

    Napier.base(DebugAntilog())
    log(tag = "Napier") { "Application Started" }

    val koinApplication = startKoin {
        includes(annotatedKoinConfiguration<SharedKoinApplication>())
        properties(
            mapOf(
                COUNTRY_RESOURCE_PROPERTY to countryResource,
                SETTINGS_PREFERENCES_PATH_PROPERTY to
                    "${preferencesDirectory.trimEnd('/')}/$SETTINGS_PREFERENCES_FILE_NAME",
            ),
        )
    }
    return SharedApplication(koinApplication.koin).also {
        runningApplication = it
    }
}

fun mapsApiKey(): String = BuildKonfig.MAPS_API_KEY
