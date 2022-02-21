package me.konyaco.keeptally.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import me.konyaco.keeptally.component.MainViewModel
import me.konyaco.keeptally.ui.component.HomeTopBar
import me.konyaco.keeptally.ui.component.HomeTopBarState
import me.konyaco.keeptally.ui.detail.AddRecord
import me.konyaco.keeptally.ui.detail.DetailScreen
import me.konyaco.keeptally.ui.theme.AndroidKeepTallyTheme

@OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun App(viewModel: MainViewModel) {
    AndroidKeepTallyTheme {
        val sheet = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
        val scope = rememberCoroutineScope()
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.inverseOnSurface,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            ModalBottomSheetLayout(
                sheetState = sheet,
                sheetContent = {
                    AddRecord(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        viewModel = viewModel,
                        onCloseRequest = { scope.launch { sheet.hide() } }
                    )
                },
                sheetShape = RectangleShape
            ) {
                Column(Modifier.fillMaxSize()) {
                    val homeTopBarState = remember {
                        HomeTopBarState { year, month ->
                            viewModel.setDateRange(MainViewModel.DateRange.Month(year, month))
                        }
                    }
                    val pagerState = rememberPagerState()

                    LaunchedEffect(homeTopBarState.selectedTab.value) {
                        val index = HomeTopBarState.TabItem.values()
                            .indexOf(homeTopBarState.selectedTab.value)
                        pagerState.animateScrollToPage(index)
                    }

                    LaunchedEffect(pagerState.currentPage) {
                        val tab = HomeTopBarState.TabItem.values()[pagerState.currentPage]
                        homeTopBarState.selectTab(tab)
                    }

                    HomeTopBar(Modifier.fillMaxWidth(), homeTopBarState)
                    HorizontalPager(
                        count = remember { HomeTopBarState.TabItem.values().size },
                        state = pagerState
                    ) {
                        when (it) {
                            0 -> DetailScreen(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                onAddClick = { scope.launch { sheet.show() } },
                                viewModel = viewModel
                            )
                            1, 2, 3 -> TodoScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TodoScreen() {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Text("TODO")
    }
}