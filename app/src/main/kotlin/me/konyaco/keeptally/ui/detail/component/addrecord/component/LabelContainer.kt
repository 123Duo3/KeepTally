package me.konyaco.keeptally.ui.detail.component.addrecord.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun LabelContainer(
    modifier: Modifier,
    selected: Boolean,
    onSelectChange: (Boolean) -> Unit,
    interactionSource: MutableInteractionSource = remember {
        MutableInteractionSource()
    },
    activeColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    activeContentColor: Color = contentColorFor(activeColor),
    content: @Composable () -> Unit
) {
    val borderWidth by animateDpAsState(if (selected) 0.dp else 1.dp)
    Surface(
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onSelectChange(!selected) },
            ),
        color = animateColorAsState(
            if (selected) activeColor
            else MaterialTheme.colorScheme.surface
        ).value,
        border = if (borderWidth == 0.dp) null else BorderStroke(
            borderWidth,
            Color(0xFF757876) /* TODO */
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        CompositionLocalProvider(
            LocalContentColor provides animateColorAsState(
                if (selected) activeContentColor
                else MaterialTheme.colorScheme.onSurfaceVariant
            ).value
        ) {
            content()
        }
    }
}