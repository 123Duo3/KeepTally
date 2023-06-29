package com.konyaco.keeptally.ui.statistic.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun EmptyScreen() {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Text(text = "暂无记录", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}