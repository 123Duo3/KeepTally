package me.konyaco.keeptally.ui.detail.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.konyaco.keeptally.ui.RecordSign
import me.konyaco.keeptally.ui.component.RecordItem
import me.konyaco.keeptally.ui.formatMoneyCent
import me.konyaco.keeptally.ui.getRecordColor
import me.konyaco.keeptally.ui.theme.KeepTallyTheme
import me.konyaco.keeptally.ui.theme.RobotoSlab
import me.konyaco.keeptally.viewmodel.MainViewModel

@Composable
fun DailyRecord(
    modifier: Modifier = Modifier,
    date: String,
    expenditure: Int,
    income: Int,
    records: List<MainViewModel.Record>,
    onDeleteClick: (record: MainViewModel.Record) -> Unit
) {
    Column(modifier) {
        Total(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), date, expenditure, income
        )
        Spacer(Modifier.height(8.dp))
        var dropdown by remember { mutableStateOf<MainViewModel.Record?>(null) }

        Column(Modifier.fillMaxWidth()) {
            for (record in records) {
                val dark = isSystemInDarkTheme()
                val color = remember(record, dark) {
                    getRecordColor(record.type.colorIndex, record.type.income, dark)
                }
                RecordItem(
                    modifier = Modifier.fillMaxWidth(),
                    color = color,
                    title = record.type.label,
                    time = record.time,
                    category = record.type.parent ?: "",
                    money = record.money,
                    onClick = { /* TODO */ },
                    onLongClick = { dropdown = record }
                )
            }
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

@Composable
private fun Total(
    modifier: Modifier,
    date: String,
    expenditure: Int,
    income: Int
) {
    CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.titleSmall) {
        Row(modifier) {
            Text(text = date, modifier = Modifier.weight(1f))
            Text(
                text = moneyToString(expenditure, false),
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.RobotoSlab
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = moneyToString(income, true),
                color = MaterialTheme.colorScheme.tertiary,
                fontFamily = FontFamily.RobotoSlab
            )
        }
    }
}

@Composable
private fun moneyToString(money: Int, positive: Boolean): String {
    return derivedStateOf {
        val (integer, decimal) = formatMoneyCent(money)
        "${if (positive) RecordSign.POSITIVE else RecordSign.NEGATIVE}$integer.$decimal${RecordSign.RMB}"
    }.value
}

@Preview(showBackground = true)
@Composable
private fun DailyRecordPreview() {
    KeepTallyTheme {
        DailyRecord(
            modifier = Modifier.fillMaxWidth(),
            date = "今天",
            expenditure = 6000,
            income = 0,
            records = listOf(
                MainViewModel.Record(
                    time = "12:30",
                    type = MainViewModel.RecordType("父分类", "分类", false, 0),
                    money = -1100,
                    date = MainViewModel.Date("12-20", 0),
                    id = 0
                ),
                MainViewModel.Record(
                    time = "12:30",
                    type = MainViewModel.RecordType("父分类", "分类", false, 0),
                    money = -1100,
                    date = MainViewModel.Date("12-20", 0),
                    id = 1
                )
            ),
            onDeleteClick = {}
        )
    }
}