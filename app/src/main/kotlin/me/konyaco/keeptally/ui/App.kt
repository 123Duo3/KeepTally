package me.konyaco.keeptally.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import me.konyaco.keeptally.ui.component.HomeTopBar
import me.konyaco.keeptally.ui.component.HomeTopBarState
import me.konyaco.keeptally.ui.detail.DetailScreen
import me.konyaco.keeptally.ui.detail.component.AddRecord
import me.konyaco.keeptally.ui.filter.FilterScreen
import me.konyaco.keeptally.ui.other.OtherScreen
import me.konyaco.keeptally.ui.statistic.StatisticScreen
import me.konyaco.keeptally.ui.theme.AndroidKeepTallyTheme
import me.konyaco.keeptally.viewmodel.MainViewModel
import me.konyaco.keeptally.viewmodel.model.DateRange

@OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun App(viewModel: MainViewModel = hiltViewModel()) {
    val scope = rememberCoroutineScope()
    AndroidKeepTallyTheme {
        val systemUiController = rememberSystemUiController()

        val localFocus = LocalFocusManager.current
        val sheet = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden,
            confirmStateChange = {
                if (it == ModalBottomSheetValue.Hidden)
                    localFocus.clearFocus()
                true
            }
        )
        val surfaceColor = MaterialTheme.colorScheme.surface

        LaunchedEffect(systemUiController) {
            systemUiController.setSystemBarsColor(
                Color.Transparent,
                surfaceColor.luminance() > 0.5f
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.inverseOnSurface,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            ModalBottomSheetLayout(
                sheetState = sheet,
                sheetContent = {
                    AddRecord(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .imePadding(),
                        onCloseRequest = {
                            localFocus.clearFocus()
                            scope.launch { sheet.hide() }
                        }
                    )
                },
                sheetShape = RectangleShape
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    val homeTopBarState = remember {
                        HomeTopBarState { year, month ->
                            viewModel.setDateRange(DateRange.Month(year, month))
                        }
                    }
                    val pagerState = rememberPagerState()

                    var isScrolling by remember { mutableStateOf(false) }

                    LaunchedEffect(homeTopBarState.selectedTab.value) {
                        val index = HomeTopBarState.TabItem.values()
                            .indexOf(homeTopBarState.selectedTab.value)
                        isScrolling = true
                        pagerState.animateScrollToPage(index)
                        isScrolling = false
                    }

                    LaunchedEffect(pagerState.currentPage, isScrolling) {
                        if (!isScrolling) {
                            val tab = HomeTopBarState.TabItem.values()[pagerState.currentPage]
                            homeTopBarState.selectTab(tab)
                        }
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
                                onAddClick = { scope.launch { sheet.show() } }
                            )
                            1 -> FilterScreen()
                            2 -> StatisticScreen()
                            3 -> OtherScreen()
                        }
                    }
                }
            }
        }
    }
}