package com.konyaco.keeptally.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import com.konyaco.keeptally.ui.theme.KeepTallyTheme
import com.konyaco.keeptally.ui.theme.RobotoSlab
import com.konyaco.keeptally.viewmodel.model.RecordSign

@Composable
fun MoneyString(
    moneyStr: String,
    isIncome: Boolean,
    budget: String? = null,
    positiveColor: Color = MaterialTheme.colorScheme.tertiary,
    negativeColor: Color = MaterialTheme.colorScheme.primary
) {
    CompositionLocalProvider(
        LocalContentColor provides if (isIncome) positiveColor else negativeColor,
        LocalTextStyle provides MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.RobotoSlab)
    ) {
        Row {
            Text(
                modifier = Modifier.alignByBaseline(),
                text = if (isIncome) RecordSign.POSITIVE else RecordSign.NEGATIVE
            )
            Text(
                modifier = Modifier.alignByBaseline(),
                text = moneyStr
            )
            Text(
                modifier = Modifier.alignByBaseline(),
                text = RecordSign.RMB,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily.RobotoSlab
            )
            budget?.let {
                Text(
                    modifier = Modifier.alignByBaseline(),
                    text = "/$it",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.RobotoSlab
                )
            }
        }
    }
}

@Preview
@Composable
private fun PositivePreview() {
    KeepTallyTheme {
        MoneyString("1.00", true)
    }
}

@Preview
@Composable
private fun NegativePreview() {
    KeepTallyTheme {
        MoneyString("1.00", false)
    }
}