package me.konyaco.keeptally.ui.statistic

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import me.konyaco.keeptally.ui.statistic.component.Tab
import me.konyaco.keeptally.ui.statistic.component.TabItem
import me.konyaco.keeptally.ui.statistic.expenditure.ExpenditureScreen
import me.konyaco.keeptally.ui.statistic.income.IncomeScreen
import me.konyaco.keeptally.ui.statistic.summary.SummaryScreen
import me.konyaco.keeptally.ui.theme.KeepTallyTheme
import me.konyaco.keeptally.viewmodel.StatisticViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StatisticScreen(viewModel: StatisticViewModel = hiltViewModel()) {
    Column(Modifier
        .fillMaxSize()
        .nestedScroll(
            object : NestedScrollConnection {

            }
        )
    ) {
        var page by rememberSaveable { mutableStateOf(TabItem.TOTAL) }

        Tab(selected = page, onSelectedChange = { page = it })

        AnimatedContent(targetState = page) {
            when (it) {
                TabItem.TOTAL -> SummaryScreen()
                TabItem.EXPENDITURE -> ExpenditureScreen()
                TabItem.INCOME -> IncomeScreen()
            }
        }
    }
}


@Preview
@Composable
private fun StatisticScreenPreview() {
    KeepTallyTheme {
        Surface(color = MaterialTheme.colorScheme.inverseOnSurface) {
            StatisticScreen()
        }
    }
}