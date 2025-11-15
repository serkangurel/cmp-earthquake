package com.sgmobile.earthquake.feature.earthquake.presentation

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sgmobile.earthquake.core.navigation.LocalNavScaffoldPadding
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.earthquakes
import com.sgmobile.earthquake.core.ui.components.SGAppBar
import com.sgmobile.earthquake.core.ui.components.SGLoading
import com.sgmobile.earthquake.feature.earthquake.presentation.components.EarthquakeRowItem
import com.sgmobile.earthquake.feature.earthquake.presentation.models.EarthquakeVo
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
            EarthquakeContent(uiState)
            if (uiState.isLoading) {
                SGLoading()
            }
        }
    }
}

@Composable
private fun EarthquakeContent(uiState: EarthquakeUIState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
    ) {
        itemsIndexed(uiState.earhtquakeList) { index, item ->
            EarthquakeRowItem(item)

            if (index < uiState.earhtquakeList.size - 1) {
                HorizontalDivider(
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(vertical = 6.dp)
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
private fun EarthquakeContentPreview() {
    EarthquakeContent(
        uiState = EarthquakeUIState(
            isLoading = false,
            earhtquakeList = listOf(
                EarthquakeVo(
                    place = "San Francisco",
                    magnitude = "5.2",
                    date = "19.10.2025 14:30"
                ),
                EarthquakeVo(
                    place = "San Francisco",
                    magnitude = "5.2",
                    date = "19.10.2025 14:30"
                ),
                EarthquakeVo(
                    place = "San Francisco",
                    magnitude = "5.2",
                    date = "19.10.2025 14:30"
                ),
                EarthquakeVo(
                    place = "San Francisco",
                    magnitude = "5.2",
                    date = "19.10.2025 14:30"
                )
            )
        )
    )
}
