package com.konyaco.keeptally.viewmodel.model

import androidx.compose.runtime.Stable
import com.konyaco.keeptally.viewmodel.model.RecordSign
import java.text.DecimalFormat
import kotlin.math.abs

/**
 * @param money abs money
 */
data class Money(
    val money: Int,
    val moneyStr: MoneyString = MoneyString.fromMoney(money)
) {
    data class MoneyString(
        val integer: String,
        val decimal: String,
        val join: String = "$integer.$decimal",
    ) {
        @Stable
        fun joinWithSign(positive: Boolean): String {
            return if (positive) {
                RecordSign.POSITIVE + join
            } else {
                RecordSign.NEGATIVE + join
            }
        }

        companion object {
            fun fromMoney(money: Int): MoneyString {
                val format = formatMoneyCent(abs(money))
                return MoneyString(format.first, format.second)
            }
        }
    }
}

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