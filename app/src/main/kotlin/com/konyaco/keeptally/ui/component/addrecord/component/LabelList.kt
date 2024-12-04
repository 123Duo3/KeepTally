package com.konyaco.keeptally.ui.component.addrecord.component

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konyaco.keeptally.ui.theme.KeepTallyTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun LabelList(
    modifier: Modifier,
    labels: List<String>,
    checkedLabel: Int?,
    onLabelClick: (Int) -> Unit,
    onAddLabelClick: () -> Unit,
    labelColor: Color
) {
    Row(
        modifier = modifier.wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LazyRow(
            modifier = Modifier
                .wrapContentHeight()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(labels, key = { _, item -> item }) { index, item ->
                LabelItem(
                    modifier = Modifier.animateItem(),
                    selected = checkedLabel == index,
                    onSelectChange = { onLabelClick(index) },
                    text = item,
                    activeColor = labelColor
                )
            }
            item(key = { "Add" }) {
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
            listOf("购物", "餐饮", "洗浴")
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
