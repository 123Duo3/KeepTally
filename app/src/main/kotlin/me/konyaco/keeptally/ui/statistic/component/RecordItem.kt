package me.konyaco.keeptally.ui.statistic.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.konyaco.keeptally.ui.component.MoneyIndicator
import me.konyaco.keeptally.ui.component.MoneyString

@Composable
fun RecordItem(
    color: Color,
    title: String,
    money: Int,
    budget: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .wrapContentHeight()
            .sizeIn(minHeight = 56.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MoneyIndicator(money = money, budget = budget ?: 0, fillColor = color)
        Spacer(Modifier.width(16.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        MoneyString(
            money = money,
            budget = budget,
            positiveColor = MaterialTheme.colorScheme.primary,
            negativeColor = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(16.dp))
        Icon(
            modifier = Modifier.size(12.dp),
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Forward",
            tint = LocalContentColor.current.copy(0.5f)
        )
    }
}