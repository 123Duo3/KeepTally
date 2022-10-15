package me.konyaco.keeptally.ui.detail.component.addrecord.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
internal fun EditDescription(
    modifier: Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit
) {
    Box(modifier) {
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            value = value,
            onValueChange = onValueChange,
            decorationBox = {
                Box(
                    Modifier
                        .fillMaxWidth()
                ) {
                    it()
                    if (value.text.isEmpty()) {
                        Text("添加备注", color = LocalContentColor.current.copy(0.7f))
                    }
                }
            },
            textStyle = LocalTextStyle.current.copy(LocalContentColor.current),
            cursorBrush = SolidColor(LocalContentColor.current)
        )
    }
}