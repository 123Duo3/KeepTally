package me.konyaco.keeptally.ui.statistic.expenditure

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import me.konyaco.keeptally.ui.formatMoneyCent
import me.konyaco.keeptally.ui.statistic.component.DataItem
import me.konyaco.keeptally.ui.statistic.component.Graph
import me.konyaco.keeptally.ui.statistic.component.RecordList
import me.konyaco.keeptally.ui.theme.KeepTallyTheme
import me.konyaco.keeptally.viewmodel.StatisticViewModel

private val testData = listOf(
    DataItem(Color(0xFF5EDBBC), 1500),
    DataItem(Color(0xFF416277), 1959),
    DataItem(Color(0xFFB6FFEA), 2958),
)

@Composable
fun ExpenditureScreen(
    viewModel: StatisticViewModel = hiltViewModel()
) {
    ExpenditureScreen(100, 100)
}

@Composable
fun ExpenditureScreen(
    money: Int,
    budget: Int
) {
    Column(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(32.dp))
        Graph(
            Modifier.align(Alignment.CenterHorizontally),
            "支出",
            money,
            "/${formatMoneyCent(budget)}",
            MaterialTheme.colorScheme.primary,
            testData
        )
        Spacer(Modifier.height(32.dp))
        RecordList(Modifier.fillMaxSize())
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