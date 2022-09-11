package me.konyaco.keeptally.ui

import androidx.compose.runtime.Stable
import java.math.BigDecimal
import java.text.DecimalFormat

@Stable
fun formatMoneyCent(money: Int): Pair<String, String> {
    val integer = money / 100
    val decimal = money % 100
    val integerStr = DecimalFormat.getInstance().apply {
        isGroupingUsed = true
    }.format(integer)
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