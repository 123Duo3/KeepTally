package com.konyaco.keeptally.ui.detail.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.konyaco.keeptally.viewmodel.model.RecordSign
import com.konyaco.keeptally.ui.theme.KeepTallyTheme
import com.konyaco.keeptally.ui.theme.RobotoSlab

@Composable
fun TotalExpenditure(modifier: Modifier, integer: String, decimal: String) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = RecordSign.RMB,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.displaySmall,
            fontFamily = FontFamily.RobotoSlab,
        )
        Crossfade(
            modifier = Modifier.weight(1f),
            targetState = integer to decimal
        ) { (integer, decimal) ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    modifier = Modifier.alignByBaseline(),
                    text = "${RecordSign.NEGATIVE}$integer.",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.displaySmall,
                    fontFamily = FontFamily.RobotoSlab,
                    textAlign = TextAlign.End
                )
                Text(
                    modifier = Modifier.alignByBaseline(),
                    text = decimal,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.W800,
                    fontFamily = FontFamily.RobotoSlab,
                    textAlign = TextAlign.End
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun TotalExpenditurePreview() {
    KeepTallyTheme {
        TotalExpenditure(Modifier.fillMaxWidth(), "123", "456")
    }
}
