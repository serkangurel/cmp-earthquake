package com.sgmobile.earthquake.feature.earthquake.detail.presentation

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sgmobile.earthquake.core.navigation.LocalNavigator
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.earthquake_not_found
import com.sgmobile.earthquake.core.ui.components.loading.SGLoading
import com.sgmobile.earthquake.core.ui.theme.LocalDarkTheme
import com.sgmobile.earthquake.core.ui.util.SetSystemBarsLightAppearance
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.components.EarthquakeDetailActions
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.components.EarthquakeDetailSheet
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.components.EarthquakeMap
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.components.earthquakeShareSummary
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.models.EarthquakeDetail
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun EarthquakeDetailScreen(
    earthquakeId: String,
    viewModel: EarthquakeDetailViewModel = koinViewModel(parameters = { parametersOf(earthquakeId) })
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navigator = LocalNavigator.current
    val darkTheme = LocalDarkTheme.current
    val layoutDirection = LocalLayoutDirection.current
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    // The Sharesheet can change system-bar flags; reapply them when this screen resumes.
    key(lifecycleState) {
        SetSystemBarsLightAppearance(
            isAppearanceLightStatusBars = !darkTheme,
            isAppearanceLightNavigationBars = !darkTheme,
        )
    }
    val context = LocalContext.current
    val earthquake = uiState.earthquake
    val shareSummary = earthquake?.let { earthquakeShareSummary(it) }

    Scaffold { paddingValues ->
        // Let the map draw behind the status bar; inset only the floating controls.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    end = paddingValues.calculateEndPadding(layoutDirection),
                ),
        ) {
            when {
                uiState.isLoading -> SGLoading()
                earthquake != null -> EarthquakeDetailContent(
                    earthquake = earthquake,
                    mapContentPadding = PaddingValues(
                        // Keep the camera target clear of the floating controls.
                        top = paddingValues.calculateTopPadding() + 80.dp,
                        bottom = paddingValues.calculateBottomPadding(),
                    ),
                )
                else -> Text(
                    text = stringResource(Res.string.earthquake_not_found),
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            EarthquakeDetailActions(
                onBackClick = navigator::goBack,
                onShareClick = if (uiState.canShare && shareSummary != null) {
                    {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareSummary)
                            putExtra(Intent.EXTRA_TITLE, earthquake.place)
                        }
                        context.startActivity(Intent.createChooser(intent, null))
                    }
                } else {
                    null
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = paddingValues.calculateTopPadding()),
            )
        }
    }
}

@Composable
private fun EarthquakeDetailContent(
    earthquake: EarthquakeDetail,
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

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        EarthquakeMap(
            earthquake = earthquake,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = mapContentPadding.calculateTopPadding(),
                bottom = bottomMapPadding,
            ),
        )
        EarthquakeDetailSheet(
            earthquake = earthquake,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .heightIn(max = maxHeight * 0.55f)
                .onSizeChanged { sheetHeightPx = it.height },
        )
    }
}
