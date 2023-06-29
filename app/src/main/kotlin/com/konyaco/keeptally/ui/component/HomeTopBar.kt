package com.konyaco.keeptally.ui.component

import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.konyaco.keeptally.R
import com.konyaco.keeptally.ui.theme.KeepTallyTheme
import java.time.LocalDate

class HomeTopBarState(
    initSelectedTab: TabItem,
) {
    val dateChooser by mutableStateOf(DateChooserState())
    var selectedTab by mutableStateOf(initSelectedTab)
        private set

    enum class TabItem(@StringRes val labelRes: Int, val icon: ImageVector) {
        Detail(R.string.detail, Icons.Sharp.Article),
        Filter(R.string.filter, Icons.Sharp.FilterAlt),
        Statistics(R.string.statistics, Icons.Sharp.Leaderboard),
        Other(R.string.other, Icons.Sharp.Widgets);

        val label: String
            @Composable get() = stringResource(id = labelRes)

        companion object {
            val values = TabItem.values()
        }
    }

    fun selectTab(tabItem: TabItem) {
        if (selectedTab != tabItem) selectedTab = tabItem
    }

    companion object {
        val Saver: Saver<HomeTopBarState, *> = mapSaver(
            save = {
                val map = mutableMapOf<String, TabItem>()
                map["selected"] = it.selectedTab
                map
            },
            restore = {
                HomeTopBarState(initSelectedTab = it["selected"] as? TabItem ?: TabItem.Detail)
            }
        )
    }
}

@Composable
fun rememberHomeTopBarState(initSelectedTab: HomeTopBarState.TabItem = HomeTopBarState.TabItem.Detail): HomeTopBarState {
    return rememberSaveable(saver = HomeTopBarState.Saver) {
        HomeTopBarState(initSelectedTab)
    }
}

@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier,
    state: HomeTopBarState,
    onDateChosen: (year: Int, month: Int) -> Unit,
    onTabSelect: (HomeTopBarState.TabItem) -> Unit
) {
    Row(
        modifier = modifier.height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DateChooser(state.dateChooser, onDateChosen)
        Divider(Modifier.size(1.dp, 36.dp))
        Tabs(Modifier.weight(1f), state, onTabSelect)
    }
}

@Composable
private fun Tabs(
    modifier: Modifier,
    state: HomeTopBarState,
    onTabSelect: (HomeTopBarState.TabItem) -> Unit
) {
    BoxWithConstraints(modifier) {
        val displayText = maxWidth > 260.dp
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RectangleShape)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeTopBarState.TabItem.values.forEach {
                CustomTab(
                    selected = state.selectedTab == it,
                    icon = it.icon,
                    text = it.label,
                    displayText = displayText,
                    onClick = { if (state.selectedTab != it) onTabSelect(it) },
                )
            }
        }
    }
}

class DateChooserState {
    private val now = LocalDate.now()

    data class DateRange(
        val year: Int,
        val month: Int,
        val isCurrentYear: Boolean,
        val isCurrentMonth: Boolean,
    )

    private fun newDateRange(y: Int, m: Int): DateRange {
        return DateRange(y, m, y == now.year, y == now.year && m == now.monthValue)
    }

    private fun calcAvailableDateRange(): List<DateRange> {
        val availableDate = mutableListOf<DateRange>()

        for (y in now.year downTo now.year - 2) {
            val maxMonth = if (y == now.year) now.monthValue else 12
            for (m in maxMonth downTo 1) {
                availableDate.add(newDateRange(y, m))
            }
        }

        return availableDate
    }

    var showDateChooser by mutableStateOf(false)
        private set

    var currentRange by mutableStateOf(newDateRange(now.year, now.monthValue))

    val avaDateRange = calcAvailableDateRange()

    fun selectDate(year: Int, month: Int) {
        currentRange = newDateRange(year, month)
    }

    fun showDateChooser() {
        showDateChooser = true
    }

    fun closeDateChooser() {
        showDateChooser = false
    }
}

@Stable
private fun DateChooserState.DateRange.toString1(): String {
    return when {
        isCurrentMonth && isCurrentYear -> "本月"
        isCurrentYear -> "$month 月"
        else -> "$year 年\n$month 月"
    }
}

@Stable
private fun DateChooserState.DateRange.toString2(): String {
    return when {
        isCurrentMonth && isCurrentYear -> "本月"
        isCurrentYear -> "$month 月"
        else -> "$year 年 $month 月"
    }
}

@Composable
private fun DateChooser(
    state: DateChooserState,
    onDateChosen: (year: Int, month: Int) -> Unit,
) {
    Row(
        Modifier
            .fillMaxHeight()
            .width(110.dp)
            .clickable(remember { MutableInteractionSource() }, null) {
                state.showDateChooser()
            }
            .padding(start = 16.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Crossfade(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            targetState = state.currentRange
        ) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                RangeText(
                    modifier = Modifier.wrapContentWidth(),
                    range = it
                )
            }
        }
        Spacer(Modifier.width(4.dp))
        Icon(Icons.Sharp.ArrowDropDown, contentDescription = "Dropdown")
    }
    DropdownMenu(
        expanded = state.showDateChooser,
        onDismissRequest = { state.closeDateChooser() },
        offset = DpOffset(16.dp, 0.dp)
    ) {
        LazyColumn(
            Modifier
                .height(300.dp)
                .width(140.dp)
        ) {
            itemsIndexed(state.avaDateRange) { index, range ->
                DropdownMenuItem(
                    text = { Text(range.toString2()) },
                    onClick = {
                        state.selectDate(range.year, range.month)
                        state.closeDateChooser()
                        onDateChosen(range.year, range.month)
                    }
                )
            }
        }
    }
}

@Composable
private fun RangeText(
    range: DateChooserState.DateRange,
    modifier: Modifier = Modifier
) {
    when {
        range.isCurrentYear -> {
            Text(
                modifier = modifier,
                text = range.toString1(),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        }
        else -> {
            Text(
                modifier = modifier,
                text = range.toString1(),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
private fun RangeTextPreview() {
    KeepTallyTheme {
        Column {
            RangeText(remember { DateChooserState.DateRange(2022, 9, true, true) })
            RangeText(remember { DateChooserState.DateRange(2022, 1, true, false) })
            RangeText(remember { DateChooserState.DateRange(2019, 1, false, false) })
        }
    }
}

@Composable
private fun CustomTab(
    selected: Boolean,
    icon: ImageVector,
    text: String,
    displayText: Boolean,
    onClick: () -> Unit
) {
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
        AnimatedVisibility(
            visible = selected && displayText,
            enter = fadeIn(tween(easing = LinearOutSlowInEasing)) +
                    expandHorizontally(tween(easing = LinearOutSlowInEasing)),
            exit = fadeOut(tween(durationMillis = 80, easing = FastOutLinearInEasing)) +
                    shrinkHorizontally(tween(durationMillis = 80, easing = FastOutLinearInEasing))
        ) {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = text,
                color = contentColor,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
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
        onClick = { selected = !selected },
        displayText = true
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeTopBarPreview() {
    KeepTallyTheme {
        Surface(color = MaterialTheme.colorScheme.inverseOnSurface) {
            val state = rememberHomeTopBarState()
            HomeTopBar(Modifier.fillMaxWidth(), state, { _, _ -> }, {
                state.selectTab(it)
            })
        }
    }
}