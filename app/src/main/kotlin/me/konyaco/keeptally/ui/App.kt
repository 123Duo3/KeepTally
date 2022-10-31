package me.konyaco.keeptally.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope.SlideDirection.Companion.End
import androidx.compose.animation.AnimatedContentScope.SlideDirection.Companion.Start
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import me.konyaco.keeptally.ui.component.HomeTopBar
import me.konyaco.keeptally.ui.component.HomeTopBarState
import me.konyaco.keeptally.ui.component.rememberHomeTopBarState
import me.konyaco.keeptally.ui.detail.DetailScreen
import me.konyaco.keeptally.ui.detail.component.addrecord.AddRecord
import me.konyaco.keeptally.ui.filter.FilterScreen
import me.konyaco.keeptally.ui.other.OtherScreen
import me.konyaco.keeptally.ui.statistic.StatisticScreen
import me.konyaco.keeptally.ui.theme.AndroidKeepTallyTheme
import me.konyaco.keeptally.viewmodel.MainViewModel
import me.konyaco.keeptally.viewmodel.model.DateRange

@Composable
fun App(viewModel: MainViewModel = hiltViewModel()) {
    AndroidKeepTallyTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.inverseOnSurface,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            Main(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Main(viewModel: MainViewModel) {
    val localFocus = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val sheet = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden,
        confirmStateChange = {
            if (it == ModalBottomSheetValue.Hidden)
                localFocus.clearFocus()
            true
        }
    )
    ModalBottomSheetLayout(
        sheetState = sheet,
        sheetContent = {
            AddRecord(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                onCloseRequest = {
                    localFocus.clearFocus()
                    scope.launch { sheet.hide() }
                }
            )
        },
        sheetShape = RectangleShape
    ) {
        Content(viewModel, sheet)
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun Content(
    viewModel: MainViewModel,
    sheet: ModalBottomSheetState
) {
    ContentAnimatedContent(viewModel, sheet)
}

@Composable
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
private fun ContentAnimatedContent(
    viewModel: MainViewModel,
    sheet: ModalBottomSheetState
) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        val scope = rememberCoroutineScope()
        val homeTopBarState = rememberHomeTopBarState()
        var currentScreen by remember { mutableStateOf(HomeTopBarState.TabItem.Detail) }

        HomeTopBar(
            Modifier.fillMaxWidth(),
            homeTopBarState,
            onDateChosen = { year, month ->
                viewModel.setDateRange(DateRange.Month(year, month))
            },
            onTabSelect = {
                homeTopBarState.selectTab(it)
                currentScreen = it
            }
        )

        AnimatedContent(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            targetState = currentScreen,
            transitionSpec = {
                if (initialState < targetState) {
                    ContentTransform(
                        targetContentEnter = slideIntoContainer(Start),
                        initialContentExit = slideOutOfContainer(Start)
                    )
                } else {
                    ContentTransform(
                        targetContentEnter = slideIntoContainer(End),
                        initialContentExit = slideOutOfContainer(End)
                    )
                }
            }
        ) {
            when (it) {
                HomeTopBarState.TabItem.Detail -> DetailScreen(onAddClick = { scope.launch { sheet.show() } })
                HomeTopBarState.TabItem.Filter -> FilterScreen()
                HomeTopBarState.TabItem.Statistics -> StatisticScreen()
                HomeTopBarState.TabItem.Other -> OtherScreen()
            }
        }
    }
}


@Composable
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
private fun ContentPager(
    viewModel: MainViewModel,
    sheet: ModalBottomSheetState
) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        val scope = rememberCoroutineScope()
        val homeTopBarState = rememberHomeTopBarState()
        val pagerState = rememberPagerState()
        var isScrolling by remember { mutableStateOf(false) }


        /*LaunchedEffect(pagerState.currentPage, isScrolling) {
            if (!isScrolling) {
                val tab = HomeTopBarState.TabItem.values()[pagerState.currentPage]
                homeTopBarState.selectTab(tab)
            }
        }*/

        // Change tab state according to user scrolling.
        LaunchedEffect(pagerState.currentPageOffset, pagerState.currentPage, isScrolling) {
            if (!isScrolling) {
                val offset = pagerState.currentPageOffset
                val current = pagerState.currentPage
                val index = if (offset > 0.50f) {
                    current + 1
                } else if (offset < -0.50f) {
                    current - 1
                } else {
                    current
                }
                val tab = HomeTopBarState.TabItem.values()[index]
                homeTopBarState.selectTab(tab)
            }
        }

        HomeTopBar(
            Modifier.fillMaxWidth(),
            homeTopBarState,
            onDateChosen = { year, month ->
                viewModel.setDateRange(DateRange.Month(year, month))
            },
            onTabSelect = {
                scope.launch {
                    homeTopBarState.selectTab(it)
                    val index = HomeTopBarState.TabItem.values
                        .indexOf(homeTopBarState.selectedTab)
                    isScrolling = true
                    pagerState.animateScrollToPage(index)
                    isScrolling = false
                }
            }
        )


        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            count = remember { HomeTopBarState.TabItem.values.size },
            state = pagerState
        ) {
            when (it) {
                0 -> DetailScreen(onAddClick = { scope.launch { sheet.show() } }) // FIXME(2022/9/17): This will cause frequent recomposition
                1 -> FilterScreen()
                2 -> StatisticScreen()
                3 -> OtherScreen()
            }
        }
    }
}