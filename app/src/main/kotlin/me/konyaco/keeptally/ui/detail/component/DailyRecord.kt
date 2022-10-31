package me.konyaco.keeptally.ui.detail.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.konyaco.keeptally.ui.component.RecordItem
import me.konyaco.keeptally.ui.detail.parseAsString
import me.konyaco.keeptally.ui.getRecordColor
import me.konyaco.keeptally.ui.theme.KeepTallyTheme
import me.konyaco.keeptally.ui.theme.RobotoSlab
import me.konyaco.keeptally.viewmodel.MainViewModel
import me.konyaco.keeptally.viewmodel.model.Money
import me.konyaco.keeptally.viewmodel.model.RecordSign

@Composable
fun DailyRecord(
    modifier: Modifier = Modifier,
    date: String,
    expenditure: String,
    income: String,
    records: List<MainViewModel.Record>,
    onDeleteClick: (record: MainViewModel.Record) -> Unit
) {
    Column(modifier) {
        val dark = isSystemInDarkTheme()
        Total(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            date, expenditure, income
        )
        Spacer(Modifier.height(8.dp))
        var dropdown by remember { mutableStateOf<MainViewModel.Record?>(null) }

        Column(Modifier.fillMaxWidth()) {
            for (record in records) {
                val color = remember(record, dark) {
                    getRecordColor(record.type.colorIndex, record.type.income, dark)
                }
                RecordItem(
                    modifier = Modifier.fillMaxWidth(),
                    color = color,
                    title = record.type.label,
                    time = record.time,
                    category = record.type.parent ?: "",
                    money = record.money.money,
                    onClick = { /* TODO */ },
                    onLongClick = { dropdown = record },
                    moneyStr = record.money.moneyStr.join,
                    description = record.description
                )
            }
            DropdownMenu(expanded = dropdown != null, onDismissRequest = { dropdown = null }) {
                DropdownMenuItem(text = {
                    Text(text = "删除")
                }, onClick = {
                    dropdown?.let(onDeleteClick)
                    dropdown = null
                })
            }
        }
    }
}

fun LazyListScope.DailyRecord(
    isLast: Boolean,
    date: MainViewModel.Date,
    expenditure: String,
    income: String,
    records: List<MainViewModel.Record>,
    onDeleteClick: (record: MainViewModel.Record) -> Unit
) {
    // Header
    item(contentType = "header") {
        Total(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            remember(date) { date.parseAsString() }, expenditure, income
        )
        Spacer(Modifier.height(8.dp))
    }

    // Records
    items(
        items = records,
        contentType = { "record" }
    ) { record ->
        var dropdown by remember { mutableStateOf(false) }
        val dark = isSystemInDarkTheme()
        val color = remember(record, dark) {
            getRecordColor(record.type.colorIndex, record.type.income, dark)
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            RecordItem(
                modifier = Modifier.fillMaxWidth(),
                color = color,
                title = record.type.label,
                time = record.time,
                category = record.type.parent ?: "",
                money = record.money.money,
                onClick = { },
                onLongClick = { dropdown = true },
                moneyStr = record.money.moneyStr.join,
                description = record.description
            )
            DropdownMenu(expanded = dropdown, onDismissRequest = { dropdown = false }) {
                DropdownMenuItem(text = {
                    Text(text = "删除")
                }, onClick = {
                    onDeleteClick(record)
                    dropdown = false
                })
            }
        }
    }

    // Divider
    if (!isLast) item(contentType = "divider") {
        Divider(Modifier.padding(vertical = 8.dp))
    }
}

@Composable
private fun Total(
    modifier: Modifier,
    date: String,
    expenditure: String,
    income: String
) {
    Row(modifier) {
        Text(
            text = date,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = moneyToString(expenditure, false),
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FontFamily.RobotoSlab,
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = moneyToString(income, true),
            color = MaterialTheme.colorScheme.tertiary,
            fontFamily = FontFamily.RobotoSlab,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun moneyToString(money: String, positive: Boolean): String {
    return remember(money, positive) {
        "${if (positive) RecordSign.POSITIVE else RecordSign.NEGATIVE}$money${RecordSign.RMB}"
    }
}

@Preview(showBackground = true)
@Composable
private fun DailyRecordPreview() {
    KeepTallyTheme {
        DailyRecord(
            modifier = Modifier.fillMaxWidth(),
            date = "今天",
            expenditure = "6,000",
            income = "0",
            records = listOf(
                MainViewModel.Record(
                    time = "12:30",
                    type = MainViewModel.RecordType("父分类", "分类", false, 0),
                    money = Money(1100),
                    date = MainViewModel.Date("12-20", 0),
                    id = 0,
                    isIncome = true,
                    description = null
                ),
                MainViewModel.Record(
                    time = "12:30",
                    type = MainViewModel.RecordType("父分类", "分类", false, 0),
                    money = Money(-1100),
                    date = MainViewModel.Date("12-20", 0),
                    id = 1,
                    isIncome = false,
                    description = "备注"
                )
            ),
            onDeleteClick = {}
        )
    }
}