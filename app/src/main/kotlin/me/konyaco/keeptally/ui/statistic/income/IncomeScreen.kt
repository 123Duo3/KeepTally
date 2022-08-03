package me.konyaco.keeptally.ui.statistic.income

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
import me.konyaco.keeptally.ui.RecordSign
import me.konyaco.keeptally.ui.statistic.DataItem
import me.konyaco.keeptally.ui.statistic.Graph
import me.konyaco.keeptally.ui.statistic.RecordList
import me.konyaco.keeptally.ui.theme.KeepTallyTheme

private val testData = listOf(
    DataItem(Color(0xFF5EDBBC), 1500),
    DataItem(Color(0xFF416277), 1959),
    DataItem(Color(0xFFB6FFEA), 2958),
)

@Composable
fun IncomeScreen() {
    Column(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(32.dp))
        Graph(
            Modifier.align(Alignment.CenterHorizontally),
            "收入",
            RecordSign.POSITIVE + "2,333",
            "",
            MaterialTheme.colorScheme.tertiary,
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
            IncomeScreen()
        }
    }
}