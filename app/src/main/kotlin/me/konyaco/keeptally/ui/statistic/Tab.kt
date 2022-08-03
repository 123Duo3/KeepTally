package me.konyaco.keeptally.ui.statistic

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.konyaco.keeptally.R

enum class TabItem(
    @StringRes
    val label: Int
) {
    TOTAL(R.string.statistic_tab_total),
    EXPENDITURE(R.string.statistic_tab_expenditure),
    INCOME(R.string.statistic_tab_income);

    @Stable
    fun index(): Int {
        return when (this) {
            TOTAL -> 0
            EXPENDITURE -> 1
            INCOME -> 2
        }
    }
}

@Composable
fun Tab(
    selected: TabItem,
    onSelectedChange: (TabItem) -> Unit,
    modifier: Modifier = Modifier
) {
    TabRow(
        modifier = modifier,
        selectedTabIndex = selected.index(),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        indicator = {
            Box(
                Modifier
                    .tabIndicatorOffset(it[selected.index()])
                    .fillMaxWidth()
                    .height(2.dp)
            ) {
                Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .width(30.dp)
                        .height(2.dp)
                        .background(LocalContentColor.current)
                )
            }
        },
        divider = {}
    ) {
        TabItem.values().forEach {
            androidx.compose.material3.Tab(
                modifier = Modifier.height(48.dp),
                selected = selected == it,
                onClick = {
                    onSelectedChange(it)
                }
            ) {
                Text(text = stringResource(id = it.label))
            }
        }
    }
}
