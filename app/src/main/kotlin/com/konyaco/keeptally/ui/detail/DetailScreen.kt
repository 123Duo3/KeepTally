package com.konyaco.keeptally.ui.detail

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.konyaco.keeptally.R
import com.konyaco.keeptally.ui.LocalSheetState
import com.konyaco.keeptally.ui.detail.component.AddRecordButton
import com.konyaco.keeptally.ui.detail.component.DailyRecord
import com.konyaco.keeptally.ui.detail.component.LineChart
import com.konyaco.keeptally.ui.detail.component.MoreInfo
import com.konyaco.keeptally.ui.detail.component.TotalExpenditure
import com.konyaco.keeptally.viewmodel.MainViewModel
import com.konyaco.keeptally.viewmodel.MainViewModel.Companion.State.Done
import com.konyaco.keeptally.viewmodel.MainViewModel.Companion.State.Initializing
import com.konyaco.keeptally.viewmodel.MainViewModel.Companion.State.Loading
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DetailScreen(viewModel: MainViewModel = hiltViewModel()) {
    val sheetState = LocalSheetState.current
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {
        when (viewModel.state.value) {
            Initializing, Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            Done -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    val statistics by viewModel.statistics
                    TotalExpenditure(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        integer = statistics.expenditure.moneyStr.integer,
                        decimal = statistics.expenditure.moneyStr.decimal
                    )
                    Divider(Modifier.padding(vertical = 8.dp))
                    MoreInfo(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        budget = statistics.budget.moneyStr.join,
                        income = statistics.income.moneyStr.join
                    )
                    Spacer(Modifier.height(12.dp))
                    RecordsList(viewModel,
                        Modifier
                            .fillMaxWidth()
                            .weight(1f))
                }
                AddRecordButton(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    onAddClick = {
                        scope.launch { sheetState.show() }
                    }
                )
            }
        }
    }
}


@Composable
private fun RecordsList(
    viewModel: MainViewModel,
    modifier: Modifier
) {
    Column(modifier) {
        LineChart(Modifier.fillMaxWidth())
        val records by viewModel.records
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = MaterialTheme.colorScheme.surface
        ) {
            Crossfade(modifier = modifier, targetState = records, label = "content") {
                if (it.isEmpty()) {
                    EmptyContent(Modifier.fillMaxSize())
                } else {
                    RecordsList(
                        modifier = Modifier.fillMaxSize(),
                        records = it,
                        onDelete = { viewModel.deleteRecord(it.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyContent(modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .sizeIn(maxWidth = 500.dp, maxHeight = 500.dp),
            painter = painterResource(id = R.drawable.woman_and_pen),
            contentDescription = "Empty",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "点击「添加记录」来创建第一笔记账",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RecordsList(
    modifier: Modifier,
    records: List<MainViewModel.Companion.DailyRecord>,
    onDelete: (MainViewModel.Companion.Record) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = 16.dp + WindowInsets.navigationBars.asPaddingValues()
                .calculateBottomPadding()
        )
    ) {
        records.forEachIndexed { index, item ->
            DailyRecord(
                index == records.size - 1,
                item.date,
                item.expenditure.moneyStr.join,
                item.income.moneyStr.join,
                item.records,
                onDelete
            )
        }

        /*        itemsIndexed(
                    items = records,
                    contentType = { index, item -> 1 },
                    key = { index, item -> item.date.dateString }
                ) { index, item ->
                    DailyRecord(
                        Modifier.fillMaxWidth(),
                        item.date.parseAsString(),
                        item.expenditure.moneyStr.join,
                        item.income.moneyStr.join,
                        item.records,
                        onDelete
                    )
                    if (index < records.size - 1) Divider(Modifier.padding(vertical = 8.dp))
                }*/
    }
}

@Stable
fun MainViewModel.Companion.Date.parseAsString(): String {
    return when (daysOffset) {
        -2 -> "后天"
        -1 -> "明天"
        0 -> "今天"
        1 -> "昨天"
        2 -> "前天"
        else -> dateString
    }
}