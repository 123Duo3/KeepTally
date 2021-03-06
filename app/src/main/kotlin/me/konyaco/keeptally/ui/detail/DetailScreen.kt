package me.konyaco.keeptally.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.konyaco.keeptally.component.MainViewModel
import me.konyaco.keeptally.ui.theme.KeepTallyTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DetailScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit
) {
    val records by viewModel.records.collectAsState()
    val statistics by viewModel.statistics.collectAsState()

    Box(modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            TotalExpenditure(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                expenditure = statistics.expenditure
            )
            Divider(Modifier.padding(vertical = 8.dp))
            MoreInfo(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                budget = statistics.budget,
                income = statistics.income
            )
            Spacer(Modifier.height(12.dp))
            LineChart(Modifier.fillMaxWidth())
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = MaterialTheme.colorScheme.surface
            ) {
                LazyColumn(contentPadding = PaddingValues(16.dp)) {
                    itemsIndexed(records) { index, item ->
                        DailyRecord(
                            Modifier.fillParentMaxWidth(),
                            item.date.parseAsString(),
                            item.expenditure,
                            item.income,
                            item.records
                        )
                        if (index < records.size - 1) Divider(Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }

        ExtendedFloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            icon = { Icon(Icons.Sharp.Add, "Add Record") },
            text = { Text("????????????") },
            onClick = onAddClick
        )
    }
}

@Stable
@Composable
private fun MainViewModel.Date.parseAsString(): String {
    return when (daysOffset) {
        0 -> "??????"
        1 -> "??????"
        2 -> "??????"
        else -> dateString
    }
}

@Preview(showBackground = true)
@Composable
fun DetailPagePreview() {
    KeepTallyTheme {
//        DetailScreen()
    }
}