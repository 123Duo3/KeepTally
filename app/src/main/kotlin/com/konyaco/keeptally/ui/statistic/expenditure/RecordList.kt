package com.konyaco.keeptally.ui.statistic.expenditure

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.konyaco.keeptally.ui.getRecordColor
import com.konyaco.keeptally.ui.statistic.component.RecordItem
import com.konyaco.keeptally.viewmodel.StatisticViewModel

@Composable
fun RecordList(
    modifier: Modifier,
    data: List<StatisticViewModel.Expenditure>,
    onClick: (Int) -> Unit
) {
    Surface(
        modifier = modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        val isDark = isSystemInDarkTheme()
        Column(Modifier.fillMaxWidth()) {
            data.forEachIndexed { index, record ->
                RecordItem(
                    modifier = Modifier.fillMaxWidth(),
                    color = getRecordColor(record.color, false, isDark),
                    title = record.label,
                    money = record.money.money,
                    budget = record.budget.money,
                    onClick = {
                        onClick(index)
                    },
                    moneyStr = record.money.moneyStr.join,
                    budgetStr = record.budget.moneyStr.integer
                )
            }
        }
    }
}