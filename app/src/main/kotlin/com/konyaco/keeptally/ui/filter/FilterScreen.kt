package com.konyaco.keeptally.ui.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.konyaco.keeptally.ui.component.addrecord.component.LabelItem
import com.konyaco.keeptally.ui.detail.component.DailyRecord
import com.konyaco.keeptally.viewmodel.FilterViewModel

/**
 * 如果二级中有，则一级显示个勾
 */
@Composable
fun FilterScreen(viewModel: FilterViewModel = hiltViewModel()) {
    LaunchedEffect(viewModel) {
        viewModel.mounted()
    }

    Box(Modifier.fillMaxSize()) {
        val labelColor =
            if (viewModel.isIncome.value) MaterialTheme.colorScheme.tertiaryContainer // Income
            else MaterialTheme.colorScheme.primaryContainer

        Column {
            Row(Modifier.padding(16.dp, 8.dp)) {
                LabelItem(
                    selected = viewModel.isIncome.value == false,
                    onSelectChange = { viewModel.changeType(false) },
                    text = "支出",
                    activeColor = labelColor
                )
                Spacer(modifier = Modifier.width(16.dp))
                LabelItem(
                    selected = viewModel.isIncome.value == true,
                    onSelectChange = { viewModel.changeType(true) },
                    text = "收入",
                    activeColor = labelColor
                )
            }
            Divider(Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(viewModel.primaryTypes.value) { index, item ->
                        val current = viewModel.currentPrimaryType
                        val contains = viewModel.containedPrimaryTypes.value.contains(item)
                        LabelItem(
                            selected = current.value == item,
                            onSelectChange = { viewModel.selectPrimaryType(item) },
                            text = item,
                            activeColor = labelColor,
                            icon = if (contains) {
                                {
                                    Icon(
                                        modifier = Modifier.size(12.dp),
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected"
                                    )
                                }
                            } else null
                        )
                    }
                }
                Divider(
                    Modifier
                        .height(48.dp)
                        .width(1.dp)
                )
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                }
            }
            Divider(Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        LabelItem(
                            selected = if (viewModel.selectedPrimaryTypes.value.isNotEmpty()) {
                                viewModel.selectedSecondaryTypes.value.containsAll(viewModel.secondaryTypes.value)
                            } else {
                                false
                            },
                            onSelectChange = { viewModel.toggleSelectAll() },
                            text = "全选",
                            activeColor = labelColor
                        )
                    }
                    itemsIndexed(viewModel.secondaryTypes.value) { index, item ->
                        val selected = viewModel.selectedSecondaryTypes.value.contains(item)
                        LabelItem(
                            selected = selected,
                            onSelectChange = { viewModel.selectSecondaryType(item, it) },
                            text = item,
                            activeColor = labelColor,
                            icon = if (selected) {
                                {
                                    Icon(
                                        modifier = Modifier.size(12.dp),
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected"
                                    )
                                }
                            } else null
                        )
                    }
                }
                Divider(
                    Modifier
                        .height(48.dp)
                        .width(1.dp)
                )
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        bottom = 16.dp + WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding()
                    ),
                ) {
                    val records = viewModel.records.value
                    records.forEachIndexed { index, item ->
                        DailyRecord(
                            index == records.size - 1,
                            item.date,
                            item.expenditure.moneyStr.join,
                            item.income.moneyStr.join,
                            item.records,
                            onDeleteClick = {}
                        )
                    }
                }
            }
        }
    }
}