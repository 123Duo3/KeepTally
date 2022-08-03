package me.konyaco.keeptally.ui.statistic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import me.konyaco.keeptally.R
import me.konyaco.keeptally.ui.component.MoneyString

@Composable
fun RecordList(modifier: Modifier) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            Modifier
                .fillMaxWidth()) {
            RecordItem(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                title = "支出",
                money = -1100,
                onClick = {}
            )
            RecordItem(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.tertiary,
                title = "收入",
                money = -1100,
                onClick = {}
            )
            Divider(Modifier.padding(vertical = 8.dp, horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(0.12f))
            RecordItem(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface,
                title = "结余",
                money = -1100,
                onClick = {}
            )
        }
    }

}

@Composable
fun RecordItem(
    color: Color,
    title: String,
    money: Int,
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
        MoneyString(money = money, positiveColor = color, negativeColor = color)
        Spacer(Modifier.width(16.dp))
        Icon(
            modifier = Modifier.size(12.dp),
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Forward",
            tint = LocalContentColor.current.copy(0.5f)
        )
    }
}