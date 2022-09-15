package me.konyaco.keeptally.ui.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import me.konyaco.keeptally.R
import me.konyaco.keeptally.ui.detail.component.DailyRecord
import me.konyaco.keeptally.ui.theme.KeepTallyTheme
import me.konyaco.keeptally.viewmodel.MainViewModel

@Composable
fun DetailScreen(
    viewModel: MainViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit
) {
    val statistics by viewModel.statistics.collectAsState()
    val records by viewModel.records.collectAsState()

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

            if (records.isEmpty()) {
                EmptyContent(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            } else {
                Content(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    records,
                    {
                        viewModel.deleteRecord(it.id)
                    }
                )
            }
        }

        ExtendedFloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 16.dp,
                    bottom = 16.dp + WindowInsets.navigationBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                ),
            icon = { Icon(Icons.Sharp.Add, "Add Record") },
            text = { Text("添加记录") },
            onClick = onAddClick
        )
    }
}

@Composable
private fun EmptyContent(modifier: Modifier) {
    Column(modifier) {
        LineChart(Modifier.fillMaxWidth())
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .sizeIn(maxWidth = 500.dp, maxHeight = 500.dp),
                    painter = painterResource(id = R.drawable.woman_and_pen),
                    contentDescription = "Empty",
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
                Spacer(Modifier.height(8.dp))
                Text(text = "点击「添加记录」来创建第一笔记账", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier,
    records: List<MainViewModel.DailyRecord>,
    onDelete: (MainViewModel.Record) -> Unit
) {
    Column(modifier) {
        LineChart(Modifier.fillMaxWidth())
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = MaterialTheme.colorScheme.surface
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = 16.dp,
                    bottom = 16.dp + WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding()
                )
            ) {
                itemsIndexed(records) { index, item ->
                    DailyRecord(
                        Modifier.fillParentMaxWidth(),
                        item.date.parseAsString(),
                        item.expenditure,
                        item.income,
                        item.records,
                        onDelete
                    )
                    if (index < records.size - 1) Divider(Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Stable
@Composable
private fun MainViewModel.Date.parseAsString(): String {
    return when (daysOffset) {
        0 -> "今天"
        1 -> "昨天"
        2 -> "前天"
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