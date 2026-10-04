package com.sgmobile.earthquake.feature.earthquake.overview.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sgmobile.earthquake.core.navigation.LocalNavScaffoldPadding
import com.sgmobile.earthquake.core.navigation.LocalNavigator
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.no_earthquakes_found
import com.sgmobile.earthquake.core.resource.no_earthquakes_found_description
import com.sgmobile.earthquake.core.ui.components.loading.SGLoading
import com.sgmobile.earthquake.core.ui.components.preview.PreviewThemes
import com.sgmobile.earthquake.core.ui.components.preview.SGPreview
import com.sgmobile.earthquake.core.ui.util.DisableNavigationBarContrastEnforcement
import com.sgmobile.earthquake.core.ui.util.SetSystemBarsLightAppearance
import com.sgmobile.earthquake.feature.earthquake.navigation.EarthquakeRoutes
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.components.CountrySelectionContent
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.components.EarthquakeFilters
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.components.EarthquakeRowItem
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.components.EmptyResultsContent
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.models.EarthquakeListItem
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EarthquakeScreen(
    viewModel: EarthquakeViewModel = koinViewModel<EarthquakeViewModel>()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navigator = LocalNavigator.current
    val darkTheme = isSystemInDarkTheme()
    val layoutDirection = LocalLayoutDirection.current
    SetSystemBarsLightAppearance(
        isAppearanceLightStatusBars = !darkTheme,
        isAppearanceLightNavigationBars = !darkTheme,
    )
    val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var isFilterSheetVisible by rememberSaveable { mutableStateOf(false) }
    val onFilterClick = { isFilterSheetVisible = true }
    val dismissCountrySheet: () -> Unit = {
        coroutineScope.launch {
            filterSheetState.hide()
            isFilterSheetVisible = false
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    top = paddingValues.calculateTopPadding(),
                    end = paddingValues.calculateEndPadding(layoutDirection),
                    bottom = LocalNavScaffoldPadding.current.calculateBottomPadding()
                ),
        ) {
            EarthquakeContent(
                uiState = uiState,
                onIntent = viewModel::handleIntent,
                onCountryClick = onFilterClick,
                onEarthquakeClick = {
                    navigator.navigate(EarthquakeRoutes.Detail(it.id))
                }
            )
            if (uiState.showsBlockingLoader) {
                SGLoading()
            }
        }
    }

    if (isFilterSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { isFilterSheetVisible = false },
            sheetState = filterSheetState,
        ) {
            DisableNavigationBarContrastEnforcement()
            CountrySelectionContent(
                countries = uiState.countries,
                selectedCountry = uiState.selectedCountry,
                onCountryClick = { country ->
                    if (country != uiState.selectedCountry) {
                        viewModel.handleIntent(EarthquakeScreenIntent.SelectCountry(country))
                    }
                    dismissCountrySheet()
                },
                onDismiss = dismissCountrySheet,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EarthquakeContent(
    uiState: EarthquakeOverviewState,
    onIntent: (EarthquakeScreenIntent) -> Unit,
    onEarthquakeClick: (EarthquakeListItem) -> Unit,
    onCountryClick: () -> Unit = {},
    filterBarState: TopAppBarState = rememberTopAppBarState(),
) {
    val lazyListState = rememberLazyListState()
    val density = LocalDensity.current
    val useStackedLayout = density.fontScale >= 1.5f
    var filterHeight by remember { mutableIntStateOf(0) }
    val filterScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = filterBarState,
        canScroll = {
            !useStackedLayout && uiState.canCollapseFilters &&
                (lazyListState.canScrollForward || lazyListState.canScrollBackward || filterBarState.heightOffset < 0f)
        },
    )
    LaunchedEffect(uiState.earthquakes.isEmpty(), useStackedLayout) {
        if (uiState.earthquakes.isEmpty() || useStackedLayout) {
            filterBarState.heightOffset = 0f
        }
    }
    val lastEarthquakeKey = uiState.earthquakes.lastOrNull()?.let { "earthquake:${it.id}" }
    LaunchedEffect(lazyListState, lastEarthquakeKey) {
        if (lastEarthquakeKey != null) {
            snapshotFlow {
                lazyListState.layoutInfo.visibleItemsInfo.any { it.key == lastEarthquakeKey }
            }.distinctUntilChanged().collect { isLastEarthquakeVisible ->
                if (isLastEarthquakeVisible) {
                    onIntent(EarthquakeScreenIntent.LoadMore)
                }
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .nestedScroll(filterScrollBehavior.nestedScrollConnection),
    ) {
        if (!useStackedLayout) {
            EarthquakeFilters(
                selectedCountry = uiState.selectedCountry,
                selectedMagnitude = uiState.selectedMagnitude,
                magnitudeOptions = uiState.magnitudeOptions,
                onCountryClick = onCountryClick,
                onMagnitudeClick = { onIntent(EarthquakeScreenIntent.SelectMagnitude(it)) },
                modifier = Modifier
                    .clipToBounds()
                    .layout { measurable, constraints ->
                        // Measure the full header even while its visible height collapses.
                        val placeable = measurable.measure(constraints)
                        filterBarState.heightOffsetLimit = -placeable.height.toFloat()
                        filterBarState.heightOffset = filterBarState.heightOffset.coerceIn(
                            filterBarState.heightOffsetLimit, 0f,
                        )
                        val offset = filterBarState.heightOffset.roundToInt()
                        layout(placeable.width, (placeable.height + offset).coerceAtLeast(0)) {
                            placeable.placeRelative(0, offset)
                        }
                    },
            )
        }
        BoxWithConstraints(Modifier.weight(1f)) {
            val emptyHeight = (maxHeight - 16.dp - if (useStackedLayout) {
                with(density) { filterHeight.toDp() }
            } else {
                0.dp
            }).coerceAtLeast(0.dp)
            PullToRefreshBox(
                isRefreshing = uiState.isPullToRefresh,
                onRefresh = { onIntent(EarthquakeScreenIntent.Refresh(isPullToRefresh = true)) },
                modifier = Modifier.fillMaxSize(),
                state = rememberPullToRefreshState()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState,
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    if (useStackedLayout) {
                        item(key = "filters", contentType = "filters") {
                            EarthquakeFilters(
                                selectedCountry = uiState.selectedCountry,
                                selectedMagnitude = uiState.selectedMagnitude,
                                magnitudeOptions = uiState.magnitudeOptions,
                                onCountryClick = onCountryClick,
                                onMagnitudeClick = { onIntent(EarthquakeScreenIntent.SelectMagnitude(it)) },
                                modifier = Modifier.onSizeChanged { filterHeight = it.height },
                            )
                        }
                    }
                    if (uiState.showsEmptyState) {
                        item(key = "empty", contentType = "empty") {
                            EmptyResultsContent(
                                title = stringResource(Res.string.no_earthquakes_found),
                                message = stringResource(Res.string.no_earthquakes_found_description),
                                modifier = Modifier.fillMaxWidth().heightIn(min = emptyHeight),
                            )
                        }
                    }
                    itemsIndexed(
                        items = uiState.earthquakes,
                        key = { _, item -> "earthquake:${item.id}" },
                        contentType = { _, _ -> "earthquake" },
                    ) { index, item ->
                        EarthquakeRowItem(model = item, onClick = { onEarthquakeClick(item) })
                        if (index < uiState.earthquakes.lastIndex) {
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                    if (uiState.showsPagingLoader) {
                        item(key = "loading", contentType = "loading") {
                            SGLoading(Modifier.fillMaxWidth().height(64.dp))
                        }
                    }
                }
            }
        }
    }
}

@PreviewThemes
@Composable
private fun EarthquakeContentPreview(collapsed: Boolean = false) {
    SGPreview {
        EarthquakeContent(
            uiState = EarthquakeOverviewState(
                isLoading = false,
                isPullToRefresh = false,
                isEndReached = false,
                selectedMagnitude = MagnitudeThreshold.TWO_PLUS,
                selectedCountry = null,
                countries = emptyList(),
                earthquakes = listOf(
                    EarthquakeListItem(
                        id = "1",
                        place = "San Francisco",
                        magnitude = "2.5",
                        magnitudeThreshold = MagnitudeThreshold.TWO_PLUS,
                        date = "19.10.2025 14:30"
                    ),
                    EarthquakeListItem(
                        id = "2",
                        place = "San Francisco",
                        magnitude = "5.2",
                        magnitudeThreshold = MagnitudeThreshold.FIVE_PLUS,
                        date = "19.10.2025 14:30"
                    ),
                    EarthquakeListItem(
                        id = "3",
                        place = "San Francisco",
                        magnitude = "4.7",
                        magnitudeThreshold = MagnitudeThreshold.FOUR_PLUS,
                        date = "19.10.2025 14:30"
                    ),
                    EarthquakeListItem(
                        id = "4",
                        place = "San Francisco",
                        magnitude = "8.2",
                        magnitudeThreshold = MagnitudeThreshold.FIVE_PLUS,
                        date = "19.10.2025 14:30"
                    )
                )
            ),
            onIntent = {},
            onEarthquakeClick = {},
            filterBarState = rememberTopAppBarState(
                initialHeightOffset = if (collapsed) -Float.MAX_VALUE else 0f,
            ),
        )
    }
}

@PreviewThemes
@Composable
private fun EarthquakeContentCollapsedPreview() {
    EarthquakeContentPreview(collapsed = true)
}
