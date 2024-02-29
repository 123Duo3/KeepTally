package com.konyaco.keeptally.ui.component.addrecord.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.konyaco.keeptally.ui.theme.KeepTallyTheme

@Composable
internal fun LabelItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onSelectChange: (Boolean) -> Unit,
    text: String,
    icon: @Composable (() -> Unit)? = null,
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
        Row(Modifier.padding(12.dp, 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(8.dp))
            }
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


