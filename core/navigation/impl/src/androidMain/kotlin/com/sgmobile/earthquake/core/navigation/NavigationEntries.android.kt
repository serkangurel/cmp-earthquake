package com.sgmobile.earthquake.core.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator

@Composable
internal fun NavigationSnapshot.toEntries(
    entryProvider: (NavigationDestination) -> NavEntry<NavigationDestination>,
): List<NavEntry<NavigationDestination>> {
    val decoratedEntries = backStacks.associate { stack ->
        val decorators = listOf<NavEntryDecorator<NavigationDestination>>(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        )
        val scopedProvider = { destination: NavigationDestination ->
            val entry = entryProvider(destination)
            NavEntry(
                key = destination,
                contentKey = scopedContentKey(stack.root, entry.contentKey),
                metadata = entry.metadata,
            ) { entry.Content() }
        }
        stack.root.key to rememberDecoratedNavEntries(
            backStack = stack.destinations,
            entryDecorators = decorators,
            entryProvider = scopedProvider,
        )
    }
    val rootsInUse = if (selectedRoot.key == startRoute.key) {
        listOf(startRoute.key)
    } else {
        listOf(startRoute.key, selectedRoot.key)
    }
    return rootsInUse.flatMap { decoratedEntries[it].orEmpty() }
}

// Each tab owns its entry state, even when two tabs display the same earthquake ID.
internal fun scopedContentKey(root: NavigationDestination, contentKey: Any): String =
    "${root.key}::$contentKey"
