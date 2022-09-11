package me.konyaco.keeptally.ui.statistic.summary

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
import me.konyaco.keeptally.ui.statistic.component.DataItem
import me.konyaco.keeptally.ui.statistic.component.Graph
import me.konyaco.keeptally.ui.theme.KeepTallyTheme

private val testData = listOf(
    DataItem(Color(0xFF5EDBBC), 1500),
    DataItem(Color(0xFF416277), 1959),
    DataItem(Color(0xFFB6FFEA), 2958),
)

@Composable
fun SummaryScreen(expenditure: Int, income: Int, balance: Int) {
    Column(Modifier.fillMaxSize()) {
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
            SummaryScreen(2333, 11, 11)
        }
    }
}