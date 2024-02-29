package com.konyaco.keeptally.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konyaco.keeptally.ui.theme.KeepTallyTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecordItem(
    color: Color,
    title: String,
    time: String,
    category: String,
    money: Long,
    moneyStr: String,
    description: String?,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MoneyIndicator(money = money, budget = 0, fillColor = color)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                if (description != null)
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "· $description",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LocalContentColor.current.copy(0.9f),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
            }
            Text(
                text = remember { "$time $category" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        MoneyString(moneyStr, money > 0)
    }
}

@Preview(showBackground = true)
@Composable
private fun OutcomeRecordItemPreview() {
    KeepTallyTheme {
        RecordItem(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            title = "消费",
            time = "12:30",
            category = "分类",
            money = -1100,
            moneyStr = "11.00",
            description = "备注",
            onClick = {},
            onLongClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun IncomeRecordItemPreview() {
    KeepTallyTheme {
        RecordItem(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            title = "工资",
            time = "12:30",
            category = "分类",
            money = 1000000,
            moneyStr = "10,000.00",
            description = "备注",
            onClick = {},
            onLongClick = {}
        )
    }
}