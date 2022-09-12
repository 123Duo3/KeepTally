package me.konyaco.keeptally.ui.statistic.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import me.konyaco.keeptally.ui.RecordSign
import me.konyaco.keeptally.ui.formatMoneyCentToString
import me.konyaco.keeptally.ui.theme.RobotoSlab
import kotlin.math.abs

@Composable
fun Graph(
    modifier: Modifier,
    label: String,
    money: Int,
    caption: String,
    color: Color,
    data: List<DataItem>
) {
    Box(
        modifier
            .requiredSizeIn(maxHeight = 360.dp, maxWidth = 360.dp)
            .fillMaxWidth()
            .aspectRatio(1f),
    ) {
        CircleLineChart(
            modifier = Modifier.fillMaxSize(),
            data = data
        )
        ConstraintLayout(Modifier.fillMaxSize()) {
            val (labelRef, moneyRef, budgetRef) = createRefs()
            Text(
                modifier = Modifier.constrainAs(labelRef) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(moneyRef.top)
                },
                text = label,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(Modifier.constrainAs(moneyRef) { centerTo(parent) }) {
                Text(
                    modifier = Modifier.alignByBaseline(),
                    text = (if (money >= 0) RecordSign.POSITIVE else RecordSign.NEGATIVE)
                            + formatMoneyCentToString(abs(money)),
                    color = color,
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    modifier = Modifier.alignByBaseline(),
                    text = RecordSign.RMB,
                    color = color,
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = FontFamily.RobotoSlab
                )
            }

            Text(
                modifier = Modifier.constrainAs(budgetRef) {
                    centerHorizontallyTo(moneyRef)
                    top.linkTo(moneyRef.bottom)
                },
                text = caption,
                color = color,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.W400
            )
        }
    }
}
