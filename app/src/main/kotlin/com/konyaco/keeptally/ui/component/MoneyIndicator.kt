package com.konyaco.keeptally.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konyaco.keeptally.ui.theme.KeepTallyTheme

@Composable
fun MoneyIndicator(
    money: Long,
    budget: Long,
    fillColor: Color,
    gapColor: Color = MaterialTheme.colorScheme.inverseOnSurface,
    overflowColor: Color = MaterialTheme.colorScheme.error
) {
    val progress = remember(budget, money) {
        if (budget == 0L) 1f else (money.toDouble() / budget).toFloat().coerceAtMost(2f)
    }
    Canvas(
        Modifier
            .size(4.dp, 32.dp)
            .background(gapColor)
    ) {
        val height = size.height * progress.coerceAtMost(1f)
        drawRect(
            color = fillColor,
            topLeft = Offset(x = 0f, y = size.height - height),
            size = size.copy(height = height)
        )
        if (progress > 1f) {
            drawRect(
                color = overflowColor,
                size = size.copy(height = size.height * (progress - 1f))
            )
        }
    }
}

@Preview
@Composable
private fun Preview1() {
    KeepTallyTheme {
        MoneyIndicator(money = 100, budget = 120, fillColor = MaterialTheme.colorScheme.primary)
    }
}

@Preview
@Composable
private fun Preview2() {
    KeepTallyTheme {
        MoneyIndicator(money = 120, budget = 100, fillColor = MaterialTheme.colorScheme.primary)
    }
}

@Preview
@Composable
private fun Preview3() {
    KeepTallyTheme {
        MoneyIndicator(money = 50, budget = 100, fillColor = MaterialTheme.colorScheme.primary)
    }
}

@Preview
@Composable
private fun Preview4() {
    KeepTallyTheme {
        MoneyIndicator(money = 50, budget = 0, fillColor = MaterialTheme.colorScheme.primary)
    }
}