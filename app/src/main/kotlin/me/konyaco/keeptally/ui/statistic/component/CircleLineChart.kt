package me.konyaco.keeptally.ui.statistic.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

data class DataItem(
    val color: Color,
    val value: Int
)

@Composable
fun CircleLineChart(
    modifier: Modifier,
    data: List<DataItem>
) {
    Box(modifier) {
        val sum = remember(data) { data.sumOf { it.value } }
        val width = with(LocalDensity.current) {
            4.dp.toPx()
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize(),
            onDraw = {
                var start = 0f;
                data.forEach {
                    val angle = 360f * it.value / sum
                    drawArc(
                        color = it.color,
                        startAngle = start + 1f,
                        sweepAngle = angle - 1f,
                        useCenter = false,
                        style = Stroke(width = width)
                    )
                    start += angle
                }
            }
        )
    }
}