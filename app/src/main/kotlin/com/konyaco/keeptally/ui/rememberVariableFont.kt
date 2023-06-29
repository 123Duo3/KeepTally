package com.konyaco.keeptally.ui

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Typeface

@OptIn(ExperimentalTextApi::class)
@Composable
fun rememberVariableFont(
    fontName: String,
    weight: FontWeight
): FontFamily {
    val context = LocalContext.current
    val assets = remember(context) { context.assets }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val builder = remember(fontName) {
            android.graphics.Typeface.Builder(assets, "fonts/${fontName}_variable.ttf")
        }
        val typeface by produceState(
            initialValue = builder.build(),
            key1 = weight,
            producer = {
                builder.setFontVariationSettings("'wght' ${weight.weight}")
                builder.setWeight(weight.weight)
                value = builder.build()
            }
        )
        return FontFamily(Typeface(typeface))
    } else {
        return FontFamily.Default
    }
}