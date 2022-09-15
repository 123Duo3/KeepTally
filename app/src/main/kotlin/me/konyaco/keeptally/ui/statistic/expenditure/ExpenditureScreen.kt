package me.konyaco.keeptally.ui.statistic.expenditure

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import me.konyaco.keeptally.ui.formatMoneyCentToString
import me.konyaco.keeptally.ui.getRecordColor
import me.konyaco.keeptally.ui.statistic.component.DataItem
import me.konyaco.keeptally.ui.statistic.component.EmptyScreen
import me.konyaco.keeptally.ui.statistic.component.Graph
import me.konyaco.keeptally.ui.theme.KeepTallyTheme
import me.konyaco.keeptally.viewmodel.StatisticViewModel

@Composable
fun ExpenditureScreen(
    viewModel: StatisticViewModel = hiltViewModel()
) {
    val exp by viewModel.expenditures.collectAsState()
    val summary by viewModel.summary.collectAsState()
    val isDark = isSystemInDarkTheme()

    if (exp.isEmpty())
        EmptyScreen()
    else Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
    ) {
        Spacer(Modifier.height(32.dp))
        Graph(
            Modifier.align(Alignment.CenterHorizontally),
            "支出",
            summary.expenditure,
            "/${formatMoneyCentToString(summary.budget)}",
            MaterialTheme.colorScheme.primary,
            remember(exp) {
                exp.map {
                    val color = getRecordColor(it.color, false, isDark)
                    DataItem(color, it.money)
                }
            }
        )
        Spacer(Modifier.height(32.dp))
        RecordList(Modifier.fillMaxSize(), exp, onClick = {
            // TODO: Navigate to detail
        })
    }
}

@Preview
@Composable
private fun Preview() {
    KeepTallyTheme {
        Surface(color = MaterialTheme.colorScheme.inverseOnSurface) {
            ExpenditureScreen()
        }
    }
}