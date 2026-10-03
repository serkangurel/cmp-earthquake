package com.sgmobile.earthquake.feature.map.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sgmobile.earthquake.core.navigation.LocalNavScaffoldPadding
import com.sgmobile.earthquake.core.navigation.LocalNavigator
import com.sgmobile.earthquake.core.ui.util.SetSystemBarsLightAppearance
import com.sgmobile.earthquake.feature.earthquake.map.presentation.EarthquakeMapState
import com.sgmobile.earthquake.feature.earthquake.map.presentation.EarthquakeMapViewModel
import com.sgmobile.earthquake.feature.earthquake.navigation.earthquakeDetailRoute
import com.sgmobile.earthquake.feature.map.presentation.components.EarthquakeMapStatus
import com.sgmobile.earthquake.feature.map.presentation.components.EarthquakeMapSummary
import com.sgmobile.earthquake.feature.map.presentation.components.EarthquakeOverviewMap
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun MapScreen(viewModel: EarthquakeMapViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navigator = LocalNavigator.current
    val darkTheme = isSystemInDarkTheme()
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    val layoutDirection = LocalLayoutDirection.current
    val navigationPadding = LocalNavScaffoldPadding.current
    key(lifecycleState) {
        SetSystemBarsLightAppearance(!darkTheme, !darkTheme)
    }
    Scaffold { padding ->
        MapContent(
            state = state,
            safePadding = PaddingValues(
                top = padding.calculateTopPadding(),
                bottom = navigationPadding.calculateBottomPadding(),
            ),
            onEarthquakeSelected = viewModel::selectEarthquake,
            onSelectionDismissed = viewModel::dismissSelection,
            onViewDetails = { navigator.navigate(earthquakeDetailRoute(it)) },
            modifier = Modifier.fillMaxSize().padding(
                start = padding.calculateStartPadding(layoutDirection),
                end = padding.calculateEndPadding(layoutDirection),
            ),
        )
    }
}

@Composable
internal fun MapContent(
    state: EarthquakeMapState,
    safePadding: PaddingValues,
    onEarthquakeSelected: (String) -> Unit,
    onSelectionDismissed: () -> Unit,
    onViewDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = state.selectedEarthquake?.earthquake
    var summaryHeightPx by remember { mutableIntStateOf(0) }
    val summaryHeight = with(LocalDensity.current) { summaryHeightPx.toDp() }
    val bottomPadding = safePadding.calculateBottomPadding()
    BoxWithConstraints(modifier) {
        EarthquakeOverviewMap(
            state = state,
            contentPadding = PaddingValues(
                top = safePadding.calculateTopPadding(),
                bottom = bottomPadding + if (selected != null) summaryHeight + 32.dp else 0.dp,
            ),
            onEarthquakeSelected = onEarthquakeSelected,
            onSelectionDismissed = onSelectionDismissed,
            modifier = Modifier.fillMaxSize(),
        )
        if (selected != null) {
            val safeHeight = (maxHeight - safePadding.calculateTopPadding() - bottomPadding - 32.dp)
                .coerceAtLeast(0.dp)
            EarthquakeMapSummary(
                earthquake = selected,
                onDismiss = onSelectionDismissed,
                onViewDetails = { onViewDetails(selected.id) },
                modifier = Modifier.align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 16.dp, bottom = bottomPadding + 16.dp)
                    .heightIn(max = safeHeight * 0.45f)
                    .onSizeChanged { summaryHeightPx = it.height },
            )
        }
        EarthquakeMapStatus(
            snapshot = state.snapshot,
            modifier = Modifier.align(
                if (state.snapshot.pins.isEmpty()) Alignment.Center else Alignment.BottomStart,
            ).padding(safePadding).padding(16.dp),
        )
    }
}
