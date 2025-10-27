package com.sgmobile.earthquake.feature.earthquake.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sgmobile.earthquake.core.navigation.LocalNavScaffoldPadding
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.earthquakes
import com.sgmobile.earthquake.core.ui.components.SGAppBar
import com.sgmobile.earthquake.core.ui.components.SGLoading
import com.sgmobile.earthquake.feature.earthquake.presentation.components.EarthquakeRowItem
import com.sgmobile.earthquake.feature.earthquake.presentation.models.EarthquakeRowItemModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun EarthquakeScreen(
    viewModel: EarthquakeViewModel = koinViewModel<EarthquakeViewModel>()
) {
    Scaffold(
        topBar = {
            SGAppBar(
                screenTitle = stringResource(Res.string.earthquakes),
                actions = {
                    EarthquakeTopBarActions()
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
            viewModel.uiState.uiModel?.let { uiModel ->
                EarthquakeContent(uiModel)
            }
            if (viewModel.uiState.isLoading) {
                SGLoading()
            }
        }
    }
}

@Composable
private fun EarthquakeContent(
    uiModel: EarthquakeUiModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        itemsIndexed(uiModel.earhtquakeRowItemList) { index, item ->
            EarthquakeRowItem(item)

            if (index < uiModel.earhtquakeRowItemList.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun EarthquakeTopBarActions() {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("0+", "4+", "5+")

    SingleChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                contentPadding = PaddingValues(all = 0.dp),
                icon = {},
                onClick = { selectedIndex = index },
                selected = index == selectedIndex,
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
private fun EarthquakeContent() {
    EarthquakeContent(
        uiModel = EarthquakeUiModel(
            listOf(
                EarthquakeRowItemModel(
                    place = "San Francisco",
                    magnitude = "5.2",
                    depth = "10km",
                    date = "19.10.2025 14:30"
                ),
                EarthquakeRowItemModel(
                    place = "San Francisco",
                    magnitude = "5.2",
                    depth = "10km",
                    date = "19.10.2025 14:30"
                ),
                EarthquakeRowItemModel(
                    place = "San Francisco",
                    magnitude = "5.2",
                    depth = "10km",
                    date = "19.10.2025 14:30"
                ),
                EarthquakeRowItemModel(
                    place = "San Francisco",
                    magnitude = "5.2",
                    depth = "10km",
                    date = "19.10.2025 14:30"
                )
            )
        )
    )
}
