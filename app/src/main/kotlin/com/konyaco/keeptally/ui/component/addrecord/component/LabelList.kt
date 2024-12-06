package com.konyaco.keeptally.ui.component.addrecord.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konyaco.keeptally.ui.theme.KeepTallyTheme

data class Label(
    val id: Long,
    val label: String
)

@Composable
internal fun LabelList(
    modifier: Modifier,
    labels: List<Label>,
    checkedLabelIndex: Int?,
    onLabelClick: (Int) -> Unit,
    onAddLabelClick: () -> Unit,
    labelColor: Color
) {
    Row(
        modifier = modifier.wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LazyRow(
            modifier = Modifier.wrapContentHeight().weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(
                items = labels,
                key = { _, item -> item.id },
                contentType = { _, _ -> "record" }
            ) { index, item ->
                LabelItem(
                    modifier = Modifier.animateItem(),
                    selected = checkedLabelIndex == index,
                    onSelectChange = { onLabelClick(index) },
                    text = item.label,
                    activeColor = labelColor
                )
            }

            item(contentType = "add") {
                LabelItem(
                    modifier = Modifier.animateItem(),
                    selected = false,
                    onSelectChange = { onAddLabelClick() },
                    text = "＋",
                    activeColor = labelColor
                )
            }
        }
        VerticalDivider(Modifier.height(48.dp))
        IconButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
        }
    }
}

@Composable
@Preview
private fun PreviewLabelList() {
    KeepTallyTheme {
        val primaryLabels = remember {
            listOf(
                Label(0, "餐饮"),
                Label(1, "购物"),
                Label(2, "投资")
            )
        }
        var enabledLabel by remember { mutableIntStateOf(0) }
        LabelList(
            modifier = Modifier.fillMaxWidth(),
            primaryLabels,
            enabledLabel,
            onLabelClick = {
                enabledLabel = it
            },
            labelColor = MaterialTheme.colorScheme.tertiary,
            onAddLabelClick = {})
    }
}
