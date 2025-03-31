package com.sgmobile.earthquake.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cmp_earthquake.composeapp.generated.resources.Res
import cmp_earthquake.composeapp.generated.resources.ic_calendar
import cmp_earthquake.composeapp.generated.resources.ic_depth
import com.sgmobile.earthquake.data.model.EarthquakeRowItemModel
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun EarthquakeRowItem(
    model: EarthquakeRowItemModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .padding(start = 8.dp)
                .width(50.dp)
                .height(50.dp),
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors().copy(
                containerColor = Color.Green
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = model.magnitude,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(all = 8.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Text(
                text = model.place,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(12.dp),
                    painter = painterResource(Res.drawable.ic_calendar),
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = model.date,
                    fontSize = 12.sp
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(12.dp),
                    painter = painterResource(Res.drawable.ic_depth),
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = "${model.depth} km",
                    fontSize = 12.sp
                )
            }
        }
    }
}