package com.konyaco.keeptally.ui.other.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konyaco.keeptally.ui.theme.KeepTallyTheme

@Composable
fun OptionItem(
    icon: Painter,
    label: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(26.dp), Alignment.Center) {
                Icon(painter = icon, contentDescription = null)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(text = label, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = description, style = MaterialTheme.typography.bodyMedium,
                    color = LocalContentColor.current.copy(0.7f)
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    KeepTallyTheme {
        Surface(color = MaterialTheme.colorScheme.inverseOnSurface) {
            OptionItem(
                icon = rememberVectorPainter(Icons.Default.FileDownload),
                label = "导出数据",
                description = "导出数据为 JSON 文件",
                onClick = {}
            )
        }
    }
}