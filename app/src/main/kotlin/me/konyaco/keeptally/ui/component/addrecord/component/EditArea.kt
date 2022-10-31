package me.konyaco.keeptally.ui.component.addrecord.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.konyaco.keeptally.ui.component.addrecord.normalizeNumberString
import me.konyaco.keeptally.ui.component.addrecord.validateNumberString
import me.konyaco.keeptally.ui.theme.KeepTallyTheme
import me.konyaco.keeptally.ui.theme.RobotoSlab
import me.konyaco.keeptally.viewmodel.model.RecordSign

@Composable
internal fun EditArea(
    modifier: Modifier,
    income: Boolean,
    onIncomeChange: (Boolean) -> Unit,
    moneyStr: TextFieldValue,
    onMoneyStrChange: (TextFieldValue) -> Unit
) {
    val color = if (income) MaterialTheme.colorScheme.tertiary
    else MaterialTheme.colorScheme.primary
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onIncomeChange(!income) }) {
            Text(
                modifier = Modifier.offset(y = (-2).dp),
                text = if (income) RecordSign.POSITIVE else RecordSign.NEGATIVE,
                style = MaterialTheme.typography.displaySmall,
                color = color,
                fontFamily = FontFamily.RobotoSlab
            )
        }
        Divider(Modifier.size(1.dp, 32.dp))
        val focus = LocalFocusManager.current
        BasicTextField(
            modifier = Modifier
                .weight(1f)
                .alignByBaseline(),
            value = moneyStr,
            onValueChange = {
                if (validateNumberString(it.text)) {
                    val text = normalizeNumberString(it)
                    onMoneyStrChange(text)
                }
            },
            textStyle = MaterialTheme.typography.displaySmall.copy(
                fontFamily = FontFamily.RobotoSlab,
                color = color,
                textAlign = TextAlign.End
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focus.clearFocus()
            }),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
        )

        Spacer(Modifier.width(8.dp))
        Text(
            modifier = Modifier.alignByBaseline(),
            text = RecordSign.RMB,
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = FontFamily.RobotoSlab
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EditAreaPreview() {
    KeepTallyTheme {
        EditArea(
            Modifier.fillMaxWidth(),
            true,
            {},
            remember { TextFieldValue() },
            onMoneyStrChange = {})
    }
}