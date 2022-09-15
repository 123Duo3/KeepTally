package me.konyaco.keeptally.ui.statistic.income

import android.util.Log
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import me.konyaco.keeptally.ui.getRecordColor
import me.konyaco.keeptally.ui.statistic.component.DataItem
import me.konyaco.keeptally.ui.statistic.component.Graph
import me.konyaco.keeptally.ui.theme.KeepTallyTheme
import me.konyaco.keeptally.viewmodel.StatisticViewModel

private val testData = listOf(
    DataItem(Color(0xFF5EDBBC), 1500),
    DataItem(Color(0xFF416277), 1959),
    DataItem(Color(0xFFB6FFEA), 2958),
)

@Composable
fun IncomeScreen(viewModel: StatisticViewModel = hiltViewModel()) {
    val summary by viewModel.summary.collectAsState()
    val incomes by viewModel.incomes.collectAsState()
    val isDark = isSystemInDarkTheme()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
    ) {
        Spacer(Modifier.height(32.dp))
        Graph(
            Modifier.align(Alignment.CenterHorizontally),
            "收入",
            summary.income,
            "",
            MaterialTheme.colorScheme.tertiary,
            remember(incomes) {
                incomes.map {
                    val color = getRecordColor(it.color, true, isDark)
                    DataItem(color, it.money)
                }
            }
        )
        Spacer(Modifier.height(32.dp))
        Layout(content = { /*TODO*/ }, measurePolicy = { m, c ->
            Log.d("IncomeScreen", "IncomeScreen: $c")
            layout(0, 0) {}
        })
        RecordList(Modifier.fillMaxSize(), incomes, onClick = {
            // TODO:
        })
    }
}

@Preview
@Composable
private fun Preview() {
    KeepTallyTheme {
        Surface(color = MaterialTheme.colorScheme.inverseOnSurface) {
            IncomeScreen()
        }
    }
}