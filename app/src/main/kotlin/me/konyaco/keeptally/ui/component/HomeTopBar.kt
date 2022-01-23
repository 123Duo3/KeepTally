package me.konyaco.keeptally.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.konyaco.keeptally.ui.theme.KeepTallyTheme

@Composable
fun HomeTopBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            Modifier
                .fillMaxHeight()
                .clickable(remember { MutableInteractionSource() }, null) {
                    /* TODO */
                }
                .padding(start = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "本月", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Sharp.ArrowDropDown, contentDescription = "Dropdown")
        }
        Divider(Modifier.size(1.dp, 36.dp))
        var selected by remember { mutableStateOf(0) }
        Row(
            modifier = Modifier
                .clip(RectangleShape)
                .weight(1f)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomTab(
                selected = selected == 0,
                text = "明细",
                icon = Icons.Sharp.Article,
                onClick = { selected = 0 }
            )
            CustomTab(
                selected = selected == 1,
                text = "筛查",
                icon = Icons.Sharp.FilterAlt,
                onClick = { selected = 1 }
            )
            CustomTab(
                selected = selected == 2,
                text = "统计",
                icon = Icons.Sharp.Leaderboard,
                onClick = { selected = 2 }
            )
            CustomTab(
                selected = selected == 3,
                text = "杂项",
                icon = Icons.Sharp.Widgets,
                onClick = { selected = 3 }
            )
        }
    }
}

@Composable
private fun CustomTab(selected: Boolean, text: String, icon: ImageVector, onClick: () -> Unit) {
    val contentAlpha = if (selected) 1f else 0.7f
    val contentColor = MaterialTheme.colorScheme.onBackground.copy(contentAlpha)
    Row(
        Modifier
            .clickable(
                remember { MutableInteractionSource() },
                null,
                role = Role.Tab,
                onClick = onClick,
                onClickLabel = text
            )
            .wrapContentSize()
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, text, tint = contentColor)
        AnimatedVisibility(visible = selected) {
            Text(
                modifier = Modifier.padding(start = 24.dp),
                text = text,
                color = contentColor,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomTabPreview() {
    var selected by remember { mutableStateOf(false) }
    CustomTab(
        selected = selected,
        text = "详细",
        icon = Icons.Sharp.Article,
        onClick = { selected = !selected }
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeTopBarPreview() {
    KeepTallyTheme {
        HomeTopBar(Modifier.fillMaxWidth())
    }
}