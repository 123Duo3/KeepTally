package me.konyaco.keeptally.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.konyaco.keeptally.component.MainViewModel
import me.konyaco.keeptally.ui.RecordSign
import me.konyaco.keeptally.ui.formatMoneyCent
import me.konyaco.keeptally.ui.theme.KeepTallyTheme
import me.konyaco.keeptally.ui.theme.RobotoSlab
import kotlin.math.abs

@Composable
fun DailyRecord(
    modifier: Modifier = Modifier,
    date: String,
    expenditure: Int,
    income: Int,
    records: List<MainViewModel.Record>
) {
    Column(modifier) {
        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.titleSmall) {
            Row(Modifier.fillMaxWidth()) {
                Text(text = date, modifier = Modifier.weight(1f))
                Text(
                    text = moneyToString(expenditure, false),
                    color = MaterialTheme.colorScheme.tertiary,
                    fontFamily = FontFamily.RobotoSlab
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = moneyToString(income, true),
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.RobotoSlab
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            records.forEach { record ->
                RecordItem(
                    modifier = Modifier.fillMaxWidth(),
                    color = if (record.money >= 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                    title = record.type.label,
                    time = record.time,
                    category = record.type.parent ?: "",
                    money = record.money
                )
            }
        }
    }
}

@Composable
fun DailyRecord(
    modifier: Modifier = Modifier,
    date: String,
    expenditure: Int,
    income: Int
) {
    Column(modifier) {
        Total(date, expenditure, income)
        Spacer(Modifier.height(8.dp))
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(6) {
                RecordItem(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    title = "标题",
                    time = "12:30",
                    category = "分类",
                    money = -1100
                )
            }
        }
    }
}

@Composable
private fun Total(
    date: String,
    expenditure: Int,
    income: Int
) {
    CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.titleSmall) {
        Row(Modifier.fillMaxWidth()) {
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

@Stable
private fun moneyToString(money: Int, positive: Boolean): String {
    val (integer, decimal) = formatMoneyCent(money)
    return "${if (positive) RecordSign.POSITIVE else RecordSign.NEGATIVE}$integer.$decimal${RecordSign.RMB}"
}

@Preview(showBackground = true)
@Composable
private fun DailyRecordPreview() {
    KeepTallyTheme {
        DailyRecord(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            "今天",
            6000,
            0
        )
    }
}

@Composable
fun RecordItem(
    color: Color,
    title: String,
    time: String,
    category: String,
    money: Int,
    modifier: Modifier = Modifier
) {
    val income = money >= 0
    val moneyStr = formatMoneyCent(abs(money)).let { "${it.first}.${it.second}" }

    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(4.dp, 32.dp)
                .background(color)
        )
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "$time $category",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        CompositionLocalProvider(
            LocalContentColor provides if (income) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
            LocalTextStyle provides MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.RobotoSlab)
        ) {
            Row {
                Text(
                    modifier = Modifier.alignByBaseline(),
                    text = if (income) RecordSign.POSITIVE else RecordSign.NEGATIVE
                )
                Text(
                    modifier = Modifier.alignByBaseline(),
                    text = moneyStr
                )
                Text(
                    modifier = Modifier.alignByBaseline(),
                    text = RecordSign.RMB,
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.RobotoSlab
                )
            }
        }
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
            money = -1100
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
            money = 1000000
        )
    }
}