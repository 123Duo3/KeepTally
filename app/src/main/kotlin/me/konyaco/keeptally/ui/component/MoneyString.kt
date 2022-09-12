package me.konyaco.keeptally.ui.component

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
import me.konyaco.keeptally.ui.RecordSign
import me.konyaco.keeptally.ui.formatMoneyCent
import me.konyaco.keeptally.ui.formatMoneyCentToString
import me.konyaco.keeptally.ui.theme.KeepTallyTheme
import me.konyaco.keeptally.ui.theme.RobotoSlab
import kotlin.math.abs

@Composable
fun MoneyString(
    money: Int,
    budget: Int? = null,
    positiveColor: Color = MaterialTheme.colorScheme.tertiary,
    negativeColor: Color = MaterialTheme.colorScheme.primary
) {
    val income = money >= 0
    val moneyStr = formatMoneyCentToString(abs(money))
    CompositionLocalProvider(
        LocalContentColor provides if (income) positiveColor else negativeColor,
        LocalTextStyle provides MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.RobotoSlab)
    ) {
        Row {
            Text(
                modifier = Modifier.alignByBaseline(),
                text = if (income) RecordSign.POSITIVE else RecordSign.NEGATIVE
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
                    text = "/" + formatMoneyCent(it).first,
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
        MoneyString(money = 100)
    }
}

@Preview
@Composable
private fun NegativePreview() {
    KeepTallyTheme {
        MoneyString(money = -100)
    }
}