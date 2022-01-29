package me.konyaco.keeptally.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import kotlinx.coroutines.launch
import me.konyaco.keeptally.component.MainViewModel
import me.konyaco.keeptally.ui.component.HomeTopBar
import me.konyaco.keeptally.ui.detail.AddRecord
import me.konyaco.keeptally.ui.detail.DetailScreen
import me.konyaco.keeptally.ui.theme.AndroidKeepTallyTheme

@OptIn(ExperimentalMaterialApi::class)
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
                    HomeTopBar(Modifier.fillMaxWidth())
                    DetailScreen(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        onAddClick = { scope.launch { sheet.show() } },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}