package com.sgmobile.earthquake.feature.earthquake.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sgmobile.earthquake.core.navigation.LocalNavScaffoldPadding
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.earthquakes
import com.sgmobile.earthquake.core.ui.components.loading.SGLoading
import com.sgmobile.earthquake.core.ui.components.topbar.SGAppBar
import com.sgmobile.earthquake.feature.earthquake.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.presentation.components.EarthquakeRowItem
import com.sgmobile.earthquake.feature.earthquake.presentation.models.EarthquakeVo
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun EarthquakeScreen(
    viewModel: EarthquakeViewModel = koinViewModel<EarthquakeViewModel>()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            SGAppBar(
                screenTitle = stringResource(Res.string.earthquakes),
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
                onIntent = viewModel::handleIntent
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
) {
    val lazyListState = rememberLazyListState()

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
                EarthquakeRowItem(item)

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

@Composable
private fun EarthquakeTopBarActions(
    onIntent: (EarthquakeScreenIntent) -> Unit,
    selectedMagnitude: MagnitudeThreshold
) {
    val options = MagnitudeThreshold.labels

    SingleChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                contentPadding = PaddingValues(all = 0.dp),
                icon = {},
                onClick = {
                    onIntent(
                        EarthquakeScreenIntent.SelectMagnitude(MagnitudeThreshold.fromLabel(label))
                    )
                },
                selected = label == selectedMagnitude.label,
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun EarthquakeContentPreview() {
    EarthquakeContent(
        uiState = EarthquakeUIState(
            isLoading = false,
            isPullToRefresh = false,
            isEndReached = false,
            selectedMagnitude = MagnitudeThreshold.TWO_PLUS,
            earhtquakeList = listOf(
                EarthquakeVo(
                    place = "San Francisco",
                    magnitude = "2.5",
                    magnitudeThreshold = MagnitudeThreshold.TWO_PLUS,
                    date = "19.10.2025 14:30"
                ),
                EarthquakeVo(
                    place = "San Francisco",
                    magnitude = "5.2",
                    magnitudeThreshold = MagnitudeThreshold.FIVE_PLUS,
                    date = "19.10.2025 14:30"
                ),
                EarthquakeVo(
                    place = "San Francisco",
                    magnitude = "4.7",
                    magnitudeThreshold = MagnitudeThreshold.FOUR_PLUS,
                    date = "19.10.2025 14:30"
                ),
                EarthquakeVo(
                    place = "San Francisco",
                    magnitude = "8.2",
                    magnitudeThreshold = MagnitudeThreshold.FIVE_PLUS,
                    date = "19.10.2025 14:30"
                )
            )
        ),
        onIntent = {}
    )
}
