package com.konyaco.keeptally.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope.SlideDirection.Companion.End
import androidx.compose.animation.AnimatedContentScope.SlideDirection.Companion.Start
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SnackbarHost
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.konyaco.keeptally.di.sharedViewModel
import com.konyaco.keeptally.ui.component.HomeTopBar
import com.konyaco.keeptally.ui.component.HomeTopBarState
import com.konyaco.keeptally.ui.component.addrecord.AddRecord
import com.konyaco.keeptally.ui.component.rememberHomeTopBarState
import com.konyaco.keeptally.ui.detail.DetailScreen
import com.konyaco.keeptally.ui.filter.FilterScreen
import com.konyaco.keeptally.ui.other.OtherScreen
import com.konyaco.keeptally.ui.statistic.StatisticScreen
import com.konyaco.keeptally.ui.theme.AndroidKeepTallyTheme
import com.konyaco.keeptally.viewmodel.MainViewModel
import com.konyaco.keeptally.viewmodel.SharedViewModel
import com.konyaco.keeptally.viewmodel.model.DateRange
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun App(
    viewModel: MainViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel = sharedViewModel()
) {
    AndroidKeepTallyTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.inverseOnSurface,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            Box(Modifier.fillMaxSize()) {
                val localFocus = rememberUpdatedState(LocalFocusManager.current)
                val scope = rememberCoroutineScope()
                val sheetState = rememberModalBottomSheetState(
                    initialValue = ModalBottomSheetValue.Hidden,
                    confirmValueChange = {
                        if (it == ModalBottomSheetValue.Hidden)
                            localFocus.value.clearFocus()
                        true
                    }
                )
                ModalBottomSheetLayout(
                    sheetState = sheetState,
                    sheetContent = {
                        AddRecord(
                            modifier = Modifier
                                .fillMaxWidth()
                                .imePadding(),
                            onCloseRequest = {
                                localFocus.value.clearFocus()
                                scope.launch { sheetState.hide() }
                            }
                        )
                    },
                    sheetShape = RectangleShape
                ) {
                    CompositionLocalProvider(LocalSheetState provides sheetState) {
                        Content(viewModel)
                    }
                }
                SnackbarHost(
                    sharedViewModel.snackbarHostState,
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Content(viewModel: MainViewModel) {
    ContentAnimatedContent(viewModel)
}

@Composable
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
private fun ContentAnimatedContent(viewModel: MainViewModel) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        HomeTopBar(
            Modifier.fillMaxWidth(),
            viewModel.homeTopBarState,
            onDateChosen = { year, month ->
                viewModel.setDateRange(DateRange.Month(year, month))
            },
            onTabSelect = {
                viewModel.homeTopBarState.selectTab(it)
            }
        )

        AnimatedContent(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            targetState = viewModel.homeTopBarState.selectedTab,
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
            }, label = "content"
        ) {
            when (it) {
                HomeTopBarState.TabItem.Detail -> DetailScreen()
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
    viewModel: MainViewModel
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
                0 -> DetailScreen()
                1 -> FilterScreen()
                2 -> StatisticScreen()
                3 -> OtherScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
val LocalSheetState = compositionLocalOf<ModalBottomSheetState> {
    error("No ModalBottomSheetState provided")
}