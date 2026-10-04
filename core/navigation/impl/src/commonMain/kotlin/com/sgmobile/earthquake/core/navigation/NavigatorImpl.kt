package com.sgmobile.earthquake.core.navigation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

fun createNavigator(
    startRoute: NavigationDestination,
    topLevelRoutes: List<NavigationDestination>,
    resolveDestination: (String) -> NavigationDestination?,
): Navigator = NavigatorImpl(startRoute, topLevelRoutes, resolveDestination)

private class NavigatorImpl(
    startRoute: NavigationDestination,
    topLevelRoutes: List<NavigationDestination>,
    private val resolver: (String) -> NavigationDestination?,
) : Navigator {
    private val roots = topLevelRoutes.toList()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val json = Json { ignoreUnknownKeys = true }
    private val mutableState: MutableStateFlow<NavigationSnapshot>

    init {
        require(roots.isNotEmpty() && roots.map { it.key }.distinct().size == roots.size)
        require(roots.any { it.key == startRoute.key })
        val start = roots.first { it.key == startRoute.key }
        mutableState = MutableStateFlow(
            NavigationSnapshot(start, start, roots.map { NavigationBackStack(it, listOf(it)) }),
        )
    }

    override val state = mutableState.asStateFlow()
    override val currentState: NavigationSnapshot
        get() = state.value

    override fun resolveDestination(key: String): NavigationDestination? = resolver(key)

    override fun navigate(destination: NavigationDestination) {
        val resolved = resolver(destination.key) ?: return
        val root = roots.firstOrNull { it.key == resolved.key }
        mutableState.update { previous ->
            if (root != null) {
                previous.copy(selectedRoot = root)
            } else {
                previous.copy(backStacks = previous.backStacks.map { stack ->
                    if (stack.root.key != previous.selectedRoot.key ||
                        stack.destinations.any { it.key == resolved.key }) {
                        stack
                    } else {
                        stack.copy(destinations = stack.destinations + resolved)
                    }
                })
            }
        }
    }

    override fun goBack() {
        mutableState.update { previous ->
            val selected = previous.backStacks.first { it.root.key == previous.selectedRoot.key }
            if (selected.destinations.size == 1) {
                previous.copy(selectedRoot = previous.startRoute)
            } else {
                previous.copy(backStacks = previous.backStacks.map { stack ->
                    if (stack.root.key == selected.root.key) {
                        stack.copy(destinations = stack.destinations.dropLast(1))
                    } else {
                        stack
                    }
                })
            }
        }
    }

    override fun setBackStack(rootKey: String, destinationKeys: List<String>) {
        val root = roots.firstOrNull { it.key == rootKey } ?: return
        val destinations = resolvePath(destinationKeys) ?: return
        mutableState.update { previous ->
            previous.copy(backStacks = previous.backStacks.map { stack ->
                if (stack.root.key == root.key) {
                    stack.copy(destinations = listOf(root) + destinations)
                } else {
                    stack
                }
            })
        }
    }

    private fun resolvePath(keys: List<String>): List<NavigationDestination>? {
        if (keys.distinct().size != keys.size) return null
        return keys.map { key ->
            if (roots.any { it.key == key }) return null
            resolver(key) ?: return null
        }
    }

    override fun observe(observer: (NavigationSnapshot) -> Unit): NavigationObservation {
        val job = scope.launch { state.collect(observer) }
        return object : NavigationObservation {
            override fun cancel() { job.cancel() }
        }
    }

    override fun saveState(): String {
        val snapshot = currentState
        return json.encodeToString(
            SavedNavigationState(
                version = 1,
                selectedRootKey = snapshot.selectedRoot.key,
                backStacks = snapshot.backStacks.map { stack ->
                    SavedBackStack(stack.root.key, stack.destinations.drop(1).map { it.key })
                },
            ),
        )
    }

    override fun restoreState(savedState: String): Boolean {
        val saved = runCatching { json.decodeFromString<SavedNavigationState>(savedState) }
            .getOrNull() ?: return false
        if (saved.version != 1 || saved.backStacks.map { it.rootKey } != roots.map { it.key }) return false
        val selected = roots.firstOrNull { it.key == saved.selectedRootKey } ?: return false
        val stacks = saved.backStacks.map { stack ->
            val root = roots.first { it.key == stack.rootKey }
            val destinations = resolvePath(stack.destinationKeys) ?: return false
            NavigationBackStack(root, listOf(root) + destinations)
        }
        mutableState.update { it.copy(selectedRoot = selected, backStacks = stacks) }
        return true
    }

    override fun close() {
        scope.cancel()
    }
}

@Serializable
private data class SavedNavigationState(
    val version: Int,
    val selectedRootKey: String,
    val backStacks: List<SavedBackStack>,
)

@Serializable
private data class SavedBackStack(
    val rootKey: String,
    val destinationKeys: List<String>,
)
