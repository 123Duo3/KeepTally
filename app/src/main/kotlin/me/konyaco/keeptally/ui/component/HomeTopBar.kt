package me.konyaco.keeptally.ui.component

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import me.konyaco.keeptally.R
import me.konyaco.keeptally.ui.theme.KeepTallyTheme
import java.time.LocalDate

class HomeTopBarState(val onDateChosen: (year: Int, month: Int) -> Unit) {
    val dateChooser = DateChooserState(onDateChosen)
    val selectedTab = mutableStateOf<TabItem>(TabItem.Detail)

    enum class TabItem(@StringRes val labelRes: Int, val icon: ImageVector) {
        Detail(R.string.detail, Icons.Sharp.Article),
        Filter(R.string.filter, Icons.Sharp.FilterAlt),
        Statistics(R.string.statistics, Icons.Sharp.Leaderboard),
        Other(R.string.other, Icons.Sharp.Widgets);

        val label: String
            @Composable get() = stringResource(id = labelRes)
    }

    fun selectTab(tabItem: TabItem) {
        selectedTab.value = tabItem
    }
}

@Composable
fun HomeTopBar(modifier: Modifier = Modifier, state: HomeTopBarState) {
    Row(
        modifier = modifier.height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DateChooser(state.dateChooser)
        Divider(Modifier.size(1.dp, 36.dp))
        Row(
            modifier = Modifier
                .clip(RectangleShape)
                .weight(1f)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeTopBarState.TabItem.values().forEach {
                CustomTab(
                    selected = state.selectedTab.value == it,
                    text = it.label,
                    icon = it.icon,
                    onClick = { state.selectTab(it) }
                )
            }
        }
    }
}

class DateChooserState(val onChosen: (year: Int, month: Int) -> Unit) {
    private val now = LocalDate.now()
    private fun calcAvailableDate(): List<Triple<Int, Int, String>> {
        val availableDate = mutableListOf<Triple<Int, Int, String>>()

        for (y in now.year downTo now.year - 2) {
            val maxMonth = if (y == now.year) now.monthValue else 12
            for (m in maxMonth downTo 1) {
                availableDate.add(Triple(y, m, buildDateString(y, m)))
            }
        }

        return availableDate
    }

    private fun buildDateString(year: Int, month: Int): String = when {
        year == now.year && month == now.monthValue -> "本月"
        year == now.year -> "$month 月"
        else -> "$year 年 $month 月"
    }

    var showDateChooser by mutableStateOf(false)
    var currentDate by mutableStateOf<Triple<Int, Int, String>>(
        Triple(
            now.year,
            now.monthValue,
            buildDateString(now.year, now.monthValue)
        )
    )

    var availableDate = calcAvailableDate()

    fun selectDate(year: Int, month: Int) {
        currentDate = Triple(year, month, buildDateString(year, month))
        onChosen(year, month)
    }
}

@Composable
private fun DateChooser(state: DateChooserState) {
    Box {
        Row(
            Modifier
                .fillMaxHeight()
                .clickable(remember { MutableInteractionSource() }, null) {
                    state.showDateChooser = true
                }
                .padding(start = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = state.currentDate.third, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Sharp.ArrowDropDown, contentDescription = "Dropdown")
        }
        DropdownMenu(
            expanded = state.showDateChooser,
            onDismissRequest = { state.showDateChooser = false },
            offset = DpOffset(16.dp, 0.dp)
        ) {
            LazyColumn(Modifier.height(300.dp).width(140.dp)) {
                itemsIndexed(state.availableDate) { index, (year, month, str)->
                    DropdownMenuItem(
                        text = { Text(str) },
                        onClick = {
                            state.selectDate(year, month)
                            state.showDateChooser = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomTab(selected: Boolean, text: String, icon: ImageVector, onClick: () -> Unit) {
    val contentAlpha = if (selected) 1f else 0.7f
    val contentColor = MaterialTheme.colorScheme.onBackground.copy(contentAlpha)
    Row(
        Modifier
            .clickable(
                remember { MutableInteractionSource() },
                null,
                role = Role.Tab,
                onClick = onClick,
                onClickLabel = text
            )
            .wrapContentSize()
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, text, tint = contentColor)
        AnimatedVisibility(visible = selected) {
            Text(
                modifier = Modifier.padding(start = 24.dp),
                text = text,
                color = contentColor,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomTabPreview() {
    var selected by remember { mutableStateOf(false) }
    CustomTab(
        selected = selected,
        text = "详细",
        icon = Icons.Sharp.Article,
        onClick = { selected = !selected }
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeTopBarPreview() {
    KeepTallyTheme {
        HomeTopBar(Modifier.fillMaxWidth(), remember { HomeTopBarState { _, _ -> } })
    }
}