package me.konyaco.keeptally.ui.statistic

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import me.konyaco.keeptally.component.MainViewModel
import me.konyaco.keeptally.ui.RecordSign
import me.konyaco.keeptally.ui.statistic.expenditure.ExpenditureScreen
import me.konyaco.keeptally.ui.statistic.income.IncomeScreen
import me.konyaco.keeptally.ui.statistic.summary.TotalScreen
import me.konyaco.keeptally.ui.theme.KeepTallyTheme

@Composable
fun StatisticScreen(viewModel: MainViewModel = viewModel()) {
    StatisticScreen()
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StatisticScreen() {
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
                TabItem.TOTAL -> TotalScreen()
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