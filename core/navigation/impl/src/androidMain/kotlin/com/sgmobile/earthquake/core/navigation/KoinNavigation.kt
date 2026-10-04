package com.sgmobile.earthquake.core.navigation

import androidx.compose.runtime.Composable
import org.koin.compose.navigation3.EntryProviderInstaller
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

/**
 * Registers a Koin Navigation 3 destination with an explicit, saveable content key.
 *
 * Content keys must remain stable and unique because Navigation 3 uses them to scope entry state.
 */
@KoinExperimentalAPI
inline fun <reified T : Any> Module.navigationWithContentKey(
    noinline contentKey: (T) -> Any,
    metadata: Map<String, Any> = emptyMap(),
    noinline definition: @Composable Scope.(T) -> Unit,
): KoinDefinition<EntryProviderInstaller> =
    single<EntryProviderInstaller>(qualifier = named<T>()) {
        val scope = this
        val installer: EntryProviderInstaller = {
            entry<T>(
                clazzContentKey = contentKey,
                metadata = metadata,
                content = { route -> definition(scope, route) },
            )
        }
        installer
    }
