package com.sgmobile.earthquake.feature.earthquake.detail.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sgmobile.earthquake.core.navigation.LocalNavigator
import com.sgmobile.earthquake.core.ui.components.loading.SGLoading
import com.sgmobile.earthquake.core.ui.components.preview.PreviewThemes
import com.sgmobile.earthquake.core.ui.components.preview.SGPreview
import com.sgmobile.earthquake.core.ui.components.topbar.SGAppBar
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.models.EarthquakeDetailVo
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun EarthquakeDetailScreen(
    earthquakeId: String,
    viewModel: EarthquakeDetailViewModel = koinViewModel(parameters = { parametersOf(earthquakeId) })
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navigator = LocalNavigator.current

    Scaffold(
        topBar = {
            SGAppBar(
                screenTitle = "Detail",
                onNavigationClick = navigator::goBack,
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                ),
        ) {
            val earthquake = uiState.earthquake
            when {
                uiState.isLoading -> SGLoading()
                earthquake != null -> EarthquakeDetailContent(earthquake = earthquake)
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
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EarthquakeDetailRow(label = "Place", value = earthquake.place)
        EarthquakeDetailRow(label = "Magnitude", value = earthquake.magnitude)
        EarthquakeDetailRow(label = "Depth", value = earthquake.depth)
        EarthquakeDetailRow(label = "Date", value = earthquake.date)
        EarthquakeDetailRow(label = "Latitude", value = earthquake.latitude)
        EarthquakeDetailRow(label = "Longitude", value = earthquake.longitude)
    }
}

@Composable
private fun EarthquakeDetailRow(
    label: String,
    value: String,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold
            ),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@PreviewThemes
@Composable
private fun EarthquakeDetailContentPreview() {
    SGPreview {
        EarthquakeDetailContent(
            earthquake = EarthquakeDetailVo(
                place = "San Francisco",
                magnitude = "5.2",
                depth = "10",
                date = "19.10.2025 14:30",
                latitude = "37.7",
                longitude = "-122.4"
            )
        )
    }
}
