package com.sgmobile.earthquake.feature.earthquake.detail.presentation

import android.content.Intent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sgmobile.earthquake.core.navigation.LocalNavigator
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.back_button
import com.sgmobile.earthquake.core.resource.earthquake_not_found
import com.sgmobile.earthquake.core.resource.share
import com.sgmobile.earthquake.core.ui.components.loading.SGLoading
import com.sgmobile.earthquake.core.ui.components.topbar.SGAppBar
import com.sgmobile.earthquake.core.ui.util.SetSystemBarsLightAppearance
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
    val backContentDescription = stringResource(Res.string.back_button)
    val darkTheme = isSystemInDarkTheme()
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    // The Sharesheet can change system-bar flags; reapply them when this screen resumes.
    key(lifecycleState) {
        SetSystemBarsLightAppearance(
            isAppearanceLightStatusBars = darkTheme,
            isAppearanceLightNavigationBars = !darkTheme,
        )
    }
    val context = LocalContext.current
    val earthquake = uiState.earthquake
    val shareSummary = earthquake?.let { earthquakeShareSummary(it) }

    Scaffold(
        topBar = {
            SGAppBar(
                navigationIcon = {
                    IconButton(onClick = navigator::goBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = backContentDescription,
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                },
                actions = {
                    if (shareSummary != null && !uiState.isLoading) {
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareSummary)
                                putExtra(Intent.EXTRA_TITLE, earthquake.place)
                            }
                            context.startActivity(Intent.createChooser(intent, null))
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = stringResource(Res.string.share),
                                tint = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
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
            when {
                uiState.isLoading -> SGLoading()
                earthquake != null -> EarthquakeDetailContent(
                    earthquake = earthquake,
                    mapContentPadding = PaddingValues(
                        bottom = paddingValues.calculateBottomPadding(),
                    ),
                )
                else -> Text(
                    text = stringResource(Res.string.earthquake_not_found),
                    modifier = Modifier.align(Alignment.Center),
                )
            }
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
            contentPadding = PaddingValues(bottom = bottomMapPadding),
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
