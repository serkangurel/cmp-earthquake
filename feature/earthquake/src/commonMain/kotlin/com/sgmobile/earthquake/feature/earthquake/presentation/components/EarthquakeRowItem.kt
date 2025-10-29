package com.sgmobile.earthquake.feature.earthquake.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sgmobile.earthquake.feature.earthquake.presentation.models.EarthquakeVo
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun EarthquakeRowItem(
    model: EarthquakeVo
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = model.place,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                modifier = Modifier,
                text = model.date,
                style = MaterialTheme.typography.labelMedium
            )
        }
        Text(
            modifier = Modifier,
            text = model.magnitude,
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EarthquakeRowItemPreview() {
    EarthquakeRowItem(
        model = EarthquakeVo(
            place = "San Francisco",
            magnitude = "5.2",
            date = "19.10.2025 14:30"
        )
    )
}