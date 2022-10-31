package me.konyaco.keeptally.ui.component.addrecord.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.konyaco.keeptally.ui.theme.KeepTallyTheme

@Composable
internal fun LabelItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onSelectChange: (Boolean) -> Unit,
    text: String,
    activeColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    activeContentColor: Color = contentColorFor(activeColor)
) {
    LabelContainer(
        modifier = modifier,
        selected = selected,
        onSelectChange = onSelectChange,
        activeColor = activeColor,
        activeContentColor = activeContentColor
    ) {
        Box(Modifier.padding(12.dp, 6.dp), Alignment.Center) {
            Text(
                modifier = Modifier.wrapContentSize(),
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Preview
@Composable
private fun PreviewLabel() {
    KeepTallyTheme {
        var selected by remember { mutableStateOf(false) }
        LabelItem(selected = selected, onSelectChange = { selected = it }, text = "标签")
    }
}


