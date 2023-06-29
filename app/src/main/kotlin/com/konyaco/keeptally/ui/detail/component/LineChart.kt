package com.konyaco.keeptally.ui.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp

@Composable
fun LineChart(modifier: Modifier) {
    Row(
        modifier
            .height(4.dp)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        // TODO:

        Layout(measurePolicy = { measurables, constraints ->
            val placeables = measurables.map { it.measure(constraints) } // 将父布局的 Constraints 向下传递
            layout(constraints.maxWidth, constraints.maxHeight) {
                placeables.forEach { it.place(0, 0) } // 把子组件都放置到 0, 0
            }
        })
    }
}