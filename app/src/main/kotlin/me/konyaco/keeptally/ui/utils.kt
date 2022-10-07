package me.konyaco.keeptally.ui

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import me.konyaco.keeptally.viewmodel.model.Colors
import java.math.BigDecimal

@Stable
fun parseMoneyToCent(str: String): Int {
    return BigDecimal(str).multiply(BigDecimal(100)).toInt()
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