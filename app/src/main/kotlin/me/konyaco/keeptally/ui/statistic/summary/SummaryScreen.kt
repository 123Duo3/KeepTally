package me.konyaco.keeptally.ui.statistic.summary

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
fun SummaryScreen(viewModel: StatisticViewModel = hiltViewModel()) {
    val summary by viewModel.summary.collectAsState()
    SummaryScreen(
        summary.expenditure.moneyStr.join,
        summary.income.moneyStr.join,
        summary.balance.moneyStr.joinWithSign(summary.balance.money >= 0)
    )
}

@Composable
fun SummaryScreen(
    expenditure: String,
    income: String,
    balance: String
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
    ) {
        Spacer(Modifier.height(32.dp))
        Graph(
            Modifier.align(Alignment.CenterHorizontally),
            "结余",
            balance,
            "",
            MaterialTheme.colorScheme.primary,
            testData
        )
        Spacer(Modifier.height(32.dp))
        DetailCard(Modifier.fillMaxSize(), expenditure, income, balance)
    }
}

@Preview
@Composable
private fun TotalScreenPreview() {
    KeepTallyTheme {
        Surface(color = MaterialTheme.colorScheme.inverseOnSurface) {
            SummaryScreen("23.33", "23.00", "-0.33")
        }
    }
}