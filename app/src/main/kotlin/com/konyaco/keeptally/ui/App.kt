@file:OptIn(ExperimentalMaterial3Api::class)

package com.konyaco.keeptally.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.End
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Start
import androidx.compose.animation.ContentTransform
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.konyaco.keeptally.di.sharedViewModel
import com.konyaco.keeptally.ui.component.HomeTopBar
import com.konyaco.keeptally.ui.component.HomeTopBarState
import com.konyaco.keeptally.ui.component.addrecord.AddRecord
import com.konyaco.keeptally.ui.component.rememberHomeTopBarState
import com.konyaco.keeptally.ui.detail.DetailScreen
import com.konyaco.keeptally.ui.filter.FilterScreen
import com.konyaco.keeptally.ui.other.OtherScreen
import com.konyaco.keeptally.ui.statistic.StatisticScreen
import com.konyaco.keeptally.ui.theme.KeepTallyTheme
import com.konyaco.keeptally.viewmodel.MainViewModel
import com.konyaco.keeptally.viewmodel.SharedViewModel
import com.konyaco.keeptally.viewmodel.model.DateRange
import kotlinx.coroutines.launch

@Composable
fun App(
    viewModel: MainViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel = sharedViewModel()
) {
    KeepTallyTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.inverseOnSurface,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            Box {
                var openSheet by remember { mutableStateOf(false) }
                val localFocus = rememberUpdatedState(LocalFocusManager.current)
                val scope = rememberCoroutineScope()
                val sheetState = rememberModalBottomSheetState(
                    confirmValueChange = {
                        if (it == SheetValue.Hidden)
                            localFocus.value.clearFocus()
                        true
                    },
                    skipPartiallyExpanded = true
                )
                CompositionLocalProvider(LocalSheetState provides sheetState) {
                    Content(viewModel, onAddClick = {
                        openSheet = true
                    })
                }
                SnackbarHost(
                    sharedViewModel.snackbarHostState,
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                )
                if (openSheet) ModalBottomSheet(
                    sheetState = sheetState,
                    shape = RectangleShape,
                    onDismissRequest = {
                        openSheet = false
                    },
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    AddRecord(
                        modifier = Modifier
                            .fillMaxWidth()
                            .imePadding(),
                        onCloseRequest = {
                            localFocus.value.clearFocus()
                            scope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    openSheet = false
                                }
                            }
                        }
                    )
                }
            }

        }
    }
}

@Composable
private fun Content(viewModel: MainViewModel, onAddClick: () -> Unit) {
    ContentPager(viewModel, onAddClick)
}

@Composable
private fun ContentAnimatedContent(viewModel: MainViewModel) {
    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
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
                HomeTopBarState.TabItem.Detail -> DetailScreen(onAddClick = {})
                HomeTopBarState.TabItem.Filter -> FilterScreen()
                HomeTopBarState.TabItem.Statistics -> StatisticScreen()
                HomeTopBarState.TabItem.Other -> OtherScreen()
            }
        }
    }
}


@Composable
private fun ContentPager(
    viewModel: MainViewModel,
    onAddClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        val scope = rememberCoroutineScope()
        val homeTopBarState = rememberHomeTopBarState()
        val pagerState = rememberPagerState(pageCount = { 4 })
        var isScrolling by remember { mutableStateOf(false) }


        /*LaunchedEffect(pagerState.currentPage, isScrolling) {
            if (!isScrolling) {
                val tab = HomeTopBarState.TabItem.values()[pagerState.currentPage]
                homeTopBarState.selectTab(tab)
            }
        }*/

        // Change tab state according to user scrolling.
        LaunchedEffect(pagerState.currentPageOffsetFraction, pagerState.currentPage, isScrolling) {
            if (!isScrolling) {
                val offset = pagerState.currentPageOffsetFraction
                val current = pagerState.currentPage
                val index = if (offset > 0.50f) {
                    current + 1
                } else if (offset < -0.50f) {
                    current - 1
                } else {
                    current
                }
                val tab = HomeTopBarState.TabItem.entries[index]
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
                    val index = HomeTopBarState.TabItem.entries
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
            state = pagerState,
            key = { it }
        ) {
            when (it) {
                0 -> DetailScreen(onAddClick = onAddClick)
                1 -> FilterScreen()
                2 -> StatisticScreen()
                3 -> OtherScreen()
            }
        }
    }
}

val LocalSheetState = compositionLocalOf<SheetState> {
    error("No ModalBottomSheetState provided")
}

@Composable
@Preview
private fun Test() {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var skipPartiallyExpanded by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)

    // App content
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            Modifier.toggleable(
                value = skipPartiallyExpanded,
                role = Role.Checkbox,
                onValueChange = { checked -> skipPartiallyExpanded = checked }
            )
        ) {
            Checkbox(checked = skipPartiallyExpanded, onCheckedChange = null)
            Spacer(Modifier.width(16.dp))
            Text("Skip partially expanded State")
        }
        Button(
            onClick = { openBottomSheet = !openBottomSheet },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Show Bottom Sheet")
        }
    }

    // Sheet content
    if (openBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState,
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    // Note: If you provide logic outside of onDismissRequest to remove the sheet,
                    // you must additionally handle intended state cleanup, if any.
                    onClick = {
                        scope
                            .launch { bottomSheetState.hide() }
                            .invokeOnCompletion {
                                if (!bottomSheetState.isVisible) {
                                    openBottomSheet = false
                                }
                            }
                    }
                ) {
                    Text("Hide Bottom Sheet")
                }
            }
            var text by remember { mutableStateOf("") }
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.padding(horizontal = 16.dp),
                label = { Text("Text field") }
            )
            LazyColumn {
                items(25) {
                    ListItem(
                        headlineContent = { Text("Item $it") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = "Localized description"
                            )
                        },
                        colors =
                        ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                    )
                }
            }
        }
    }
}