package com.sgmobile.earthquake.feature.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.EarthquakeTimeRange
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.settings.domain.SettingsRepository
import com.sgmobile.earthquake.feature.settings.domain.SettingsSource
import com.sgmobile.earthquake.feature.settings.domain.models.AppTheme
import com.sgmobile.earthquake.feature.settings.domain.models.SettingsPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okio.IOException
import okio.Path.Companion.toPath
import org.koin.core.annotation.Property
import org.koin.core.annotation.Single

private val THEME_KEY = stringPreferencesKey("theme")
private val DEFAULT_MAGNITUDE_KEY = stringPreferencesKey("default_magnitude")
private val DEFAULT_COUNTRY_CODE_KEY = stringPreferencesKey("default_country_code")
private val TIME_RANGE_KEY = stringPreferencesKey("time_range")

// DataStore allows only one instance per file, so the repository must stay a singleton.
@Single(binds = [SettingsRepository::class, SettingsSource::class])
internal class SettingsRepositoryImpl(
    @Property("settings.preferencesPath")
    preferencesPath: String,
) : SettingsRepository {
    // Writes outlive the screen that requested them, so they run in this app-wide scope.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        produceFile = { preferencesPath.toPath() },
    )

    override val preferences: Flow<SettingsPreferences> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { it.toSettingsPreferences() }
        .distinctUntilChanged()

    override fun setTheme(theme: AppTheme) = write(THEME_KEY, theme.name)

    override fun setDefaultMagnitude(magnitude: MagnitudeThreshold) =
        write(DEFAULT_MAGNITUDE_KEY, magnitude.name)

    override fun setDefaultCountryCode(countryCode: String) =
        write(DEFAULT_COUNTRY_CODE_KEY, countryCode)

    override fun setTimeRange(timeRange: EarthquakeTimeRange) = write(TIME_RANGE_KEY, timeRange.name)

    private fun write(key: Preferences.Key<String>, value: String) {
        scope.launch {
            try {
                dataStore.edit { it[key] = value }
            } catch (error: IOException) {
                // The stored value stays unchanged, and screens keep showing that stored value.
            }
        }
    }
}

private fun Preferences.toSettingsPreferences(): SettingsPreferences {
    val defaults = SettingsPreferences.DEFAULT
    return SettingsPreferences(
        theme = enumValue<AppTheme>(THEME_KEY),
        defaultMagnitude = enumValue<MagnitudeThreshold>(DEFAULT_MAGNITUDE_KEY) ?: defaults.defaultMagnitude,
        defaultCountryCode = this[DEFAULT_COUNTRY_CODE_KEY] ?: defaults.defaultCountryCode,
        timeRange = enumValue<EarthquakeTimeRange>(TIME_RANGE_KEY) ?: defaults.timeRange,
    )
}

private inline fun <reified T : Enum<T>> Preferences.enumValue(key: Preferences.Key<String>): T? =
    this[key]?.let { name -> enumValues<T>().firstOrNull { it.name == name } }
