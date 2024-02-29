package com.konyaco.keeptally.ui.detail.component

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddRecordButton(modifier: Modifier, onAddClick: () -> Unit) {
    val insetPaddings = WindowInsets.navigationBars.asPaddingValues()
    val paddingBottom = remember(insetPaddings) { insetPaddings.calculateBottomPadding() }

    ExtendedFloatingActionButton(
        modifier = modifier.padding(
            end = 16.dp,
            bottom = 16.dp + paddingBottom
        ),
        icon = { Icon(Icons.Sharp.Add, "Add Record") },
        text = { Text("添加记录") },
        onClick = onAddClick
    )
}