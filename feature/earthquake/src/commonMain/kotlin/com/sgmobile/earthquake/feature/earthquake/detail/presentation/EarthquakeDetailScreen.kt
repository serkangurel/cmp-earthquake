package com.sgmobile.earthquake.feature.earthquake.detail.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedrejeb.calf.ui.ExperimentalCalfUiApi
import com.mohamedrejeb.calf.ui.navigation.AdaptiveScaffold
import com.mohamedrejeb.calf.ui.navigation.UIKitUIBarButtonItem
import com.mohamedrejeb.calf.ui.navigation.UIKitUIBarButtonSystemItem
import com.sgmobile.earthquake.core.navigation.LocalNavigator
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.share
import com.sgmobile.earthquake.core.ui.components.loading.SGLoading
import com.sgmobile.earthquake.core.ui.components.topbar.SGAppBar
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.components.EarthquakeDetailSheet
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.components.EarthquakeMap
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.models.EarthquakeDetailVo
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalCalfUiApi::class)
@Composable
internal fun EarthquakeDetailScreen(
    earthquakeId: String,
    viewModel: EarthquakeDetailViewModel = koinViewModel(parameters = { parametersOf(earthquakeId) })
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navigator = LocalNavigator.current

    AdaptiveScaffold(
        topBar = {
            SGAppBar(
                onNavigationClick = navigator::goBack,
                iosTrailingItems = listOf(
                    UIKitUIBarButtonItem.systemItem(
                        systemItem = UIKitUIBarButtonSystemItem.Action,
                        onClick = {},
                    ),
                ),
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = stringResource(Res.string.share),
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        // Top padding only so the map runs full-bleed to the bottom edge.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
        ) {
            val earthquake = uiState.earthquake
            when {
                uiState.isLoading -> SGLoading()
                earthquake != null -> EarthquakeDetailContent(
                    earthquake = earthquake,
                    mapContentPadding = PaddingValues(
                        bottom = paddingValues.calculateBottomPadding(),
                    ),
                )
                else -> Text(
                    text = "Earthquake not found",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun EarthquakeDetailContent(
    earthquake: EarthquakeDetailVo,
    mapContentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    var sheetHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val sheetHeight = with(density) { sheetHeightPx.toDp() }
    val bottomMapPadding = maxOf(
        mapContentPadding.calculateBottomPadding(),
        sheetHeight,
    )

    Box(modifier = modifier.fillMaxSize()) {
        EarthquakeMap(
            earthquake = earthquake,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = bottomMapPadding),
        )
        EarthquakeDetailSheet(
            earthquake = earthquake,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .onSizeChanged { sheetHeightPx = it.height },
        )
    }
}
