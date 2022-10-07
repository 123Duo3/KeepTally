package me.konyaco.keeptally.ui.statistic.income

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.konyaco.keeptally.ui.getRecordColor
import me.konyaco.keeptally.ui.statistic.component.RecordItem
import me.konyaco.keeptally.viewmodel.StatisticViewModel

@Composable
fun RecordList(
    modifier: Modifier,
    data: List<StatisticViewModel.Income>,
    onClick: (Int) -> Unit
) {
    Surface(
        modifier = modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(Modifier.fillMaxWidth()) {
            data.forEachIndexed { index, record ->
                RecordItem(
                    modifier = Modifier.fillMaxWidth(),
                    color = getRecordColor(record.color, true, isSystemInDarkTheme()),
                    title = record.label,
                    money = record.money.money,
                    budget = null,
                    onClick = {
                        onClick(index)
                    },
                    moneyStr = record.money.moneyStr.join,
                    budgetStr = null
                )
            }
        }
    }
}