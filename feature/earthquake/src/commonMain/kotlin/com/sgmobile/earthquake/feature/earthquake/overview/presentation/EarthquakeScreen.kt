package com.sgmobile.earthquake.feature.earthquake.overview.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedrejeb.calf.ui.ExperimentalCalfUiApi
import com.mohamedrejeb.calf.ui.dropdown.AdaptiveDropDownItem
import com.mohamedrejeb.calf.ui.navigation.AdaptiveScaffold
import com.mohamedrejeb.calf.ui.navigation.UIKitUIBarButtonItem
import com.sgmobile.earthquake.core.navigation.LocalNavScaffoldPadding
import com.sgmobile.earthquake.core.navigation.LocalNavigator
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.earthquakes
import com.sgmobile.earthquake.core.ui.components.loading.SGLoading
import com.sgmobile.earthquake.core.ui.components.preview.PreviewThemes
import com.sgmobile.earthquake.core.ui.components.preview.SGPreview
import com.sgmobile.earthquake.core.ui.components.topbar.SGAppBar
import com.sgmobile.earthquake.feature.earthquake.navigation.EarthquakeRoutes
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.components.EarthquakeRowItem
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.models.EarthquakeVo
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalCalfUiApi::class)
@Composable
internal fun EarthquakeScreen(
    viewModel: EarthquakeViewModel = koinViewModel<EarthquakeViewModel>()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navigator = LocalNavigator.current
    // Reuse Calf's native UIMenu; SGAppBar updates only the visible button title on iOS.
    val iosTrailingItems = remember(viewModel) {
        listOf(
            UIKitUIBarButtonItem.withMenu(
                title = uiState.selectedMagnitude.label,
                menuItems = MagnitudeThreshold.labels.map { label ->
                    AdaptiveDropDownItem(
                        title = label,
                        onClick = {
                            viewModel.handleIntent(
                                EarthquakeScreenIntent.SelectMagnitude(
                                    MagnitudeThreshold.fromLabel(label)
                                )
                            )
                        },
                    )
                },
            ),
        )
    }

    AdaptiveScaffold(
        topBar = {
            SGAppBar(
                screenTitle = stringResource(Res.string.earthquakes),
                iosTrailingItems = iosTrailingItems,
                iosTrailingItemTitles = listOf(uiState.selectedMagnitude.label),
                actions = {
                    EarthquakeTopBarActions(
                        selectedMagnitude = uiState.selectedMagnitude,
                        onIntent = viewModel::handleIntent
                    )
                }
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = LocalNavScaffoldPadding.current.calculateBottomPadding()
                ),
        ) {
            EarthquakeContent(
                uiState = uiState,
                onIntent = viewModel::handleIntent,
                onEarthquakeClick = {
                    navigator.navigate(EarthquakeRoutes.Detail(it.id))
                }
            )
            if (uiState.isLoading) {
                SGLoading()
            }
        }
    }
}

@Composable
private fun EarthquakeContent(
    uiState: EarthquakeUIState,
    onIntent: (EarthquakeScreenIntent) -> Unit,
    onEarthquakeClick: (EarthquakeVo) -> Unit,
) {
    val lazyListState: LazyListState = rememberLazyListState()
    LaunchedEffect(uiState.earhtquakeList) {
        snapshotFlow {
            lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.distinctUntilChanged()
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex == uiState.earhtquakeList.lastIndex) {
                    onIntent(EarthquakeScreenIntent.LoadMore)
                }
            }
    }

    PullToRefreshBox(
        isRefreshing = uiState.isPullToRefresh,
        onRefresh = { onIntent(EarthquakeScreenIntent.Refresh(isPullToRefresh = true)) },
        modifier = Modifier.fillMaxSize(),
        state = rememberPullToRefreshState()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
            contentPadding = PaddingValues(16.dp),
        ) {
            itemsIndexed(uiState.earhtquakeList) { index, item ->
                EarthquakeRowItem(
                    model = item,
                    onClick = { onEarthquakeClick(item) }
                )

                if (index < uiState.earhtquakeList.size - 1) {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun EarthquakeTopBarActions(
    onIntent: (EarthquakeScreenIntent) -> Unit,
    selectedMagnitude: MagnitudeThreshold
) {
    val options = MagnitudeThreshold.labels

    Row(
        Modifier.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
    ) {
        options.forEachIndexed { index, label ->
            ToggleButton(
                checked = label == selectedMagnitude.label,
                colors = ToggleButtonDefaults.toggleButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    checkedContainerColor = MaterialTheme.colorScheme.onSecondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    checkedContentColor = MaterialTheme.colorScheme.secondary,
                ),
                onCheckedChange = {
                    onIntent(
                        EarthquakeScreenIntent.SelectMagnitude(MagnitudeThreshold.fromLabel(label))
                    )
                },
                shapes =
                    when (index) {
                        0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                        options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                    },
            ) {
                Text(label)
            }
        }
    }
}


@PreviewThemes
@Composable
private fun EarthquakeContentPreview() {
    SGPreview {
        EarthquakeContent(
            uiState = EarthquakeUIState(
                isLoading = false,
                isPullToRefresh = false,
                isEndReached = false,
                selectedMagnitude = MagnitudeThreshold.TWO_PLUS,
                earhtquakeList = listOf(
                    EarthquakeVo(
                        id = "1",
                        place = "San Francisco",
                        magnitude = "2.5",
                        magnitudeThreshold = MagnitudeThreshold.TWO_PLUS,
                        date = "19.10.2025 14:30"
                    ),
                    EarthquakeVo(
                        id = "2",
                        place = "San Francisco",
                        magnitude = "5.2",
                        magnitudeThreshold = MagnitudeThreshold.FIVE_PLUS,
                        date = "19.10.2025 14:30"
                    ),
                    EarthquakeVo(
                        id = "3",
                        place = "San Francisco",
                        magnitude = "4.7",
                        magnitudeThreshold = MagnitudeThreshold.FOUR_PLUS,
                        date = "19.10.2025 14:30"
                    ),
                    EarthquakeVo(
                        id = "4",
                        place = "San Francisco",
                        magnitude = "8.2",
                        magnitudeThreshold = MagnitudeThreshold.FIVE_PLUS,
                        date = "19.10.2025 14:30"
                    )
                )
            ),
            onIntent = {},
            onEarthquakeClick = {}
        )
    }
}
