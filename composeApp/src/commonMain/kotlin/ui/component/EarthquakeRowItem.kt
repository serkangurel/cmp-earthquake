package ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cmp_earthquake.composeapp.generated.resources.Res
import cmp_earthquake.composeapp.generated.resources.ic_calendar
import cmp_earthquake.composeapp.generated.resources.ic_clock
import cmp_earthquake.composeapp.generated.resources.ic_depth
import data.model.EarthquakeRowItemModel
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
            elevation = 4.dp,
            backgroundColor = Color.Green,
        ) {
            Text(
                modifier = Modifier.wrapContentHeight(),
                text = model.magnitude,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(all = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = model.place,
                color = Color.Black
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painterResource(Res.drawable.ic_calendar),
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = model.date,
                    color = Color.Black,
                    fontSize = 14.sp
                )

                Spacer(Modifier.width(12.dp))
                Image(
                    painterResource(Res.drawable.ic_clock),
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = model.time,
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painterResource(Res.drawable.ic_depth),
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = "${model.depth} km",
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}