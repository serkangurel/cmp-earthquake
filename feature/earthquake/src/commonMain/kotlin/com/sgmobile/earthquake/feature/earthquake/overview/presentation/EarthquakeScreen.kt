package com.sgmobile.earthquake.feature.earthquake.overview.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedrejeb.calf.sf.symbols.SFSymbol
import com.mohamedrejeb.calf.ui.ExperimentalCalfUiApi
import com.mohamedrejeb.calf.ui.dropdown.AdaptiveDropDownItem
import com.mohamedrejeb.calf.ui.gesture.adaptiveClickable
import com.mohamedrejeb.calf.ui.navigation.AdaptiveScaffold
import com.mohamedrejeb.calf.ui.navigation.UIKitUIBarButtonItem
import com.mohamedrejeb.calf.ui.sheet.AdaptiveBottomSheet
import com.mohamedrejeb.calf.ui.sheet.rememberAdaptiveSheetState
import com.mohamedrejeb.calf.ui.uikit.UIKitImage
import com.sgmobile.earthquake.core.navigation.LocalNavScaffoldPadding
import com.sgmobile.earthquake.core.navigation.LocalNavigator
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.earthquakes
import com.sgmobile.earthquake.core.resource.filter
import com.sgmobile.earthquake.core.ui.components.loading.SGLoading
import com.sgmobile.earthquake.core.ui.components.preview.PreviewThemes
import com.sgmobile.earthquake.core.ui.components.preview.SGPreview
import com.sgmobile.earthquake.core.ui.components.topbar.SGAppBar
import com.sgmobile.earthquake.feature.earthquake.navigation.EarthquakeRoutes
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.Country
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.CountryBounds
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.components.EarthquakeRowItem
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.extensions.toCountryFlagEmoji
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.models.EarthquakeVo
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val FILTER_BUTTON_CONTAINER_ALPHA = 0.20f
private const val FILTER_BUTTON_BORDER_ALPHA = 0.30f

@OptIn(ExperimentalCalfUiApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun EarthquakeScreen(
    viewModel: EarthquakeViewModel = koinViewModel<EarthquakeViewModel>()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val countries by viewModel.countries.collectAsStateWithLifecycle()
    val navigator = LocalNavigator.current
    val filterSheetState = rememberAdaptiveSheetState()
    val coroutineScope = rememberCoroutineScope()
    var isFilterSheetVisible by rememberSaveable { mutableStateOf(false) }
    val onFilterClick = { isFilterSheetVisible = true }
    val selectedCountryFlag = uiState.selectedCountry?.code?.toCountryFlagEmoji()

    val iosTrailingItems = remember(viewModel, uiState.selectedMagnitude) {
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
            val title = stringResource(Res.string.earthquakes)
            val filterContentDescription = stringResource(Res.string.filter)
            SGAppBar(
                iosTitle = title,
                navigationIcon = {
                    CountryFilterButton(
                        selectedCountryFlag = selectedCountryFlag,
                        contentDescription = filterContentDescription,
                        modifier = Modifier.padding(start = 8.dp),
                        onClick = onFilterClick,
                    )
                },
                iosLeadingItems = listOf(
                    selectedCountryFlag?.let { flag ->
                        UIKitUIBarButtonItem.title(
                            title = flag,
                            onClick = onFilterClick,
                        )
                    } ?: UIKitUIBarButtonItem.image(
                        image = UIKitImage.SystemName(SFSymbol.line3HorizontalDecrease),
                        onClick = onFilterClick,
                    ),
                ),
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

    if (isFilterSheetVisible) {
        AdaptiveBottomSheet(
            onDismissRequest = { isFilterSheetVisible = false },
            adaptiveSheetState = filterSheetState,
        ) {
            CountryList(
                countries = countries,
                selectedCountry = uiState.selectedCountry,
                onCountryClick = { country ->
                    if (country == uiState.selectedCountry) {
                        coroutineScope.launch {
                            filterSheetState.hide()
                            if (!filterSheetState.isVisible) {
                                isFilterSheetVisible = false
                            }
                        }
                    } else {
                        viewModel.handleIntent(EarthquakeScreenIntent.SelectCountry(country))
                    }
                },
            )
        }
    }
}

@Composable
private fun CountryFilterButton(
    selectedCountryFlag: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val buttonColor = MaterialTheme.colorScheme.onPrimary
    IconButton(
        onClick = onClick,
        modifier = modifier.border(
            width = 1.dp,
            color = buttonColor.copy(alpha = FILTER_BUTTON_BORDER_ALPHA),
            shape = CircleShape,
        ),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = buttonColor.copy(alpha = FILTER_BUTTON_CONTAINER_ALPHA),
            contentColor = buttonColor,
        ),
    ) {
        if (selectedCountryFlag != null) {
            Text(
                text = selectedCountryFlag,
                modifier = Modifier.clearAndSetSemantics {
                    this.contentDescription = contentDescription
                },
                style = MaterialTheme.typography.titleLarge,
            )
        } else {
            Icon(
                imageVector = Icons.Filled.FilterList,
                contentDescription = contentDescription,
            )
        }
    }
}

@PreviewThemes
@Composable
private fun CountryFilterButtonPreview() {
    SGPreview {
        val contentDescription = stringResource(Res.string.filter)
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CountryFilterButton(
                selectedCountryFlag = null,
                contentDescription = contentDescription,
                onClick = {},
            )
            CountryFilterButton(
                selectedCountryFlag = "🇹🇷",
                contentDescription = contentDescription,
                onClick = {},
            )
        }
    }
}

@Composable
private fun CountryList(
    countries: List<Country>,
    selectedCountry: Country?,
    onCountryClick: (Country) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
    ) {
        countries.forEachIndexed { index, country ->
            val isSelected = country == selectedCountry
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { selected = isSelected }
                    .adaptiveClickable { onCountryClick(country) }
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = country.code.toCountryFlagEmoji().orEmpty(),
                    modifier = Modifier
                        .width(24.dp)
                        .clearAndSetSemantics {},
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = country.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                )
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            if (index < countries.lastIndex) {
                HorizontalDivider()
            }
        }
    }
}

@PreviewThemes
@Composable
private fun CountryListPreview() {
    SGPreview {
        CountryList(
            countries = listOf(
                Country(
                    code = "TR",
                    name = "Turkey",
                    flagUrl = "",
                    bounds = CountryBounds(
                        minLongitude = 26.04,
                        minLatitude = 35.82,
                        maxLongitude = 44.79,
                        maxLatitude = 42.14,
                    ),
                ),
                Country(
                    code = "US",
                    name = "United States",
                    flagUrl = "",
                    bounds = CountryBounds(
                        minLongitude = -125.0,
                        minLatitude = 24.0,
                        maxLongitude = -66.0,
                        maxLatitude = 49.0,
                    ),
                ),
            ),
            selectedCountry = Country(
                code = "TR",
                name = "Turkey",
                flagUrl = "",
                bounds = CountryBounds(
                    minLongitude = 26.04,
                    minLatitude = 35.82,
                    maxLongitude = 44.79,
                    maxLatitude = 42.14,
                ),
            ),
            onCountryClick = {},
        )
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
                selectedCountry = null,
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
