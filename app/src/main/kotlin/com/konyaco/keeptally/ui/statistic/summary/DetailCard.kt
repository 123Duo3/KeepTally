package com.konyaco.keeptally.ui.statistic.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.konyaco.keeptally.ui.component.MoneyString

@Composable
fun DetailCard(
    modifier: Modifier,
    expStr: String,
    incomeStr: String,
    balanceStr: String,
    isBalancePositive: Boolean
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            Modifier
                .fillMaxWidth()
        ) {
            RecordItem(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                title = "支出",
                money = expStr,
                onClick = {},
                isIncome = false
            )
            RecordItem(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.tertiary,
                title = "收入",
                money = incomeStr,
                onClick = {},
                isIncome = true
            )
            Divider(
                Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(0.12f)
            )
            RecordItem(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface,
                title = "结余",
                money = balanceStr,
                onClick = {},
                isIncome = isBalancePositive
            )
        }
    }

}

@Composable
fun RecordItem(
    color: Color,
    title: String,
    isIncome: Boolean,
    money: String,
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
        Box(
            Modifier
                .size(4.dp, 32.dp)
                .background(color)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        MoneyString(money, isIncome, positiveColor = color, negativeColor = color)
        Spacer(Modifier.width(16.dp))
        Icon(
            modifier = Modifier.size(12.dp),
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Forward",
            tint = LocalContentColor.current.copy(0.5f)
        )
    }
}