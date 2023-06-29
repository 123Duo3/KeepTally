package com.konyaco.keeptally.ui.detail.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konyaco.keeptally.viewmodel.model.RecordSign
import com.konyaco.keeptally.ui.theme.KeepTallyTheme
import com.konyaco.keeptally.ui.theme.RobotoSlab

@Composable
fun MoreInfo(modifier: Modifier, budget: String, income: String) {
    Column(modifier) {
        TextRow(
            text = "预算",
            money = budget,
            sign = RecordSign.NEGATIVE,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        TextRow(
            text = "收入",
            money = income,
            sign = RecordSign.POSITIVE,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
private fun TextRow(text: String, money: String, sign: String, color: Color) {
    Row(Modifier.fillMaxWidth()) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
        Crossfade(
            modifier = Modifier.weight(1f),
            targetState = "$sign$money${RecordSign.RMB}") {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = it,
                textAlign = TextAlign.End,
                color = color,
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = FontFamily.RobotoSlab
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun MoreInfoPreview() {
    KeepTallyTheme {
        MoreInfo(Modifier.fillMaxWidth(), "1,234.56", "1,234.56")
    }
}
