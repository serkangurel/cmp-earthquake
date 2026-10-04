package com.sgmobile.earthquake.feature.settings.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sgmobile.earthquake.core.navigation.LocalNavScaffoldPadding
import com.sgmobile.earthquake.core.navigation.LocalNavigator
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.settings_time_range
import com.sgmobile.earthquake.core.resource.settings_time_range_footer
import com.sgmobile.earthquake.core.ui.components.preview.PreviewThemes
import com.sgmobile.earthquake.core.ui.components.preview.SGPreview
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.EarthquakeTimeRange
import com.sgmobile.earthquake.feature.settings.presentation.components.SettingsFooter
import com.sgmobile.earthquake.feature.settings.presentation.components.SettingsOptionGroup
import com.sgmobile.earthquake.feature.settings.presentation.components.SettingsScaffold
import com.sgmobile.earthquake.feature.settings.presentation.extensions.label
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun TimeRangeScreen(
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navigator = LocalNavigator.current
    TimeRangeContent(
        timeRangeOptions = uiState.timeRangeOptions,
        selectedTimeRange = uiState.preferences.timeRange,
        bottomPadding = LocalNavScaffoldPadding.current.calculateBottomPadding(),
        onTimeRangeSelect = { viewModel.handleIntent(SettingsIntent.SelectTimeRange(it)) },
        onBackClick = navigator::goBack,
    )
}

@Composable
internal fun TimeRangeContent(
    timeRangeOptions: List<EarthquakeTimeRange>,
    selectedTimeRange: EarthquakeTimeRange,
    bottomPadding: Dp,
    onTimeRangeSelect: (EarthquakeTimeRange) -> Unit,
    onBackClick: () -> Unit,
) {
    SettingsScaffold(
        title = stringResource(Res.string.settings_time_range),
        bottomPadding = bottomPadding,
        onBackClick = onBackClick,
    ) { contentPadding ->
        LazyColumn(Modifier.fillMaxSize(), contentPadding = contentPadding) {
            item {
                SettingsOptionGroup(
                    options = timeRangeOptions,
                    selected = selectedTimeRange,
                    label = { stringResource(it.label) },
                    onSelect = onTimeRangeSelect,
                )
            }
            item { SettingsFooter(stringResource(Res.string.settings_time_range_footer)) }
        }
    }
}

@PreviewThemes
@Composable
private fun TimeRangeContentPreview() {
    SGPreview {
        TimeRangeContent(
            timeRangeOptions = EarthquakeTimeRange.entries,
            selectedTimeRange = EarthquakeTimeRange.LAST_WEEK,
            bottomPadding = 0.dp,
            onTimeRangeSelect = {},
            onBackClick = {},
        )
    }
}
