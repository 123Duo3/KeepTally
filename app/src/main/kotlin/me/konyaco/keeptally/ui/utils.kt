package me.konyaco.keeptally.ui

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import me.konyaco.keeptally.viewmodel.model.Colors
import java.math.BigDecimal
import java.text.DecimalFormat

private val decimalFormat = DecimalFormat.getInstance().apply {
    isGroupingUsed = true
}

@Stable
fun formatMoneyCent(money: Int): Pair<String, String> {
    val integer = money / 100
    val decimal = money % 100
    val integerStr = decimalFormat.format(integer)
    val decimalStr = "%02d".format(decimal)
    return integerStr to decimalStr
}

@Stable
fun formatMoneyCentToString(money: Int): String {
    val (integer, decimal) = formatMoneyCent(money)
    return "$integer.$decimal"
}

@Stable
fun parseMoneyToCent(str: String): Int {
    return BigDecimal(str).multiply(BigDecimal(100)).toInt()
}

object RecordSign {
    const val POSITIVE = "+"
    const val NEGATIVE = "−"
    const val RMB = "¥"
}

@Stable
fun getRecordColor(
    colorIndex: Int,
    isIncome: Boolean,
    isDark: Boolean
): Color {
    val (lightColor, darkColor) =
        if (isIncome) Colors.incomeColors[colorIndex]
        else Colors.expColors[colorIndex]
    return Color(if (isDark) darkColor else lightColor)
}