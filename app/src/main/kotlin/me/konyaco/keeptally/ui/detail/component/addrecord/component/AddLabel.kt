package me.konyaco.keeptally.ui.detail.component.addrecord.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.konyaco.keeptally.ui.theme.KeepTallyTheme

@Composable
internal fun AddLabel(
    activeColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    activeContentColor: Color = contentColorFor(activeColor)
) {
    var selected by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }

    LabelContainer(
        modifier = Modifier.wrapContentSize(),
        selected = selected,
        onSelectChange = { focusRequester.requestFocus() },
        activeColor = activeColor,
        activeContentColor = activeContentColor,
        interactionSource = interactionSource
    ) {
        Box(Modifier.padding(horizontal = 12.dp), Alignment.Center) {
            var value by remember { mutableStateOf("") }
            BasicTextField(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(vertical = 6.dp)
                    .focusRequester(focusRequester)
                    .onFocusChanged { selected = it.isFocused },
                value = value,
                interactionSource = interactionSource,
                onValueChange = { value = it },
                textStyle = MaterialTheme.typography.labelLarge.copy(
                    color = LocalContentColor.current
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusRequester.freeFocus()
                    // TODO: On add label
                }),
                cursorBrush = SolidColor(LocalContentColor.current)
            )
            if (!selected) Icon(Icons.Sharp.Add, contentDescription = "Add label")
        }
    }
}


@Preview
@Composable
private fun AddLabelPreview() {
    KeepTallyTheme {
        AddLabel()
    }
}
