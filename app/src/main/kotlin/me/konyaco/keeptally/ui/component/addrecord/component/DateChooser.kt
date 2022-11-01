package me.konyaco.keeptally.ui.component.addrecord.component

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import me.konyaco.keeptally.ui.theme.KeepTallyTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.coroutines.resume

@Composable
internal fun DateChooser(
    modifier: Modifier,
    state: DateChooserState,
    onStateChange: (DateChooserState) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(modifier) {
        Crossfade(
            modifier = Modifier.fillMaxWidth(),
            targetState = state
        ) { state ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                when (state) {
                    DateChooserState.Now -> {
                        Text(
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    scope.launch {
                                        val state = with(getUserSelection(context)) {
                                            DateChooserState.Custom(
                                                year, monthValue,
                                                dayOfMonth, hour, minute
                                            )
                                        }
                                        onStateChange(state)
                                    }
                                }
                                .padding(horizontal = 4.dp),
                            text = "现在",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    is DateChooserState.Custom -> {
                        val dateText = formatDate(state)
                        val timeText = formatTime(state)

                        Text(
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    scope.launch {
                                        val date = getDate(
                                            context,
                                            LocalDate.of(state.year, state.month, state.day)
                                        )
                                        onStateChange(
                                            DateChooserState.Custom(
                                                date.year, date.monthValue, date.dayOfMonth,
                                                state.hour, state.minute
                                            )
                                        )
                                    }
                                }
                                .padding(4.dp),
                            text = dateText,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    scope.launch {
                                        val time =
                                            getTime(context, LocalTime.of(state.hour, state.minute))
                                        onStateChange(
                                            DateChooserState.Custom(
                                                state.year, state.month, state.day,
                                                time.hour, time.minute
                                            )
                                        )
                                    }
                                }
                                .padding(4.dp),
                            text = timeText,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

sealed class DateChooserState {
    object Now : DateChooserState()
    data class Custom(
        val year: Int,
        val month: Int,
        val day: Int,
        val hour: Int,
        val minute: Int,
    ) : DateChooserState()
}

@Stable
@Composable
private fun formatDate(state: DateChooserState.Custom): String = remember(state) {
    with(state) {
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            .format(LocalDate.of(year, month, day))
    }
}

@Stable
@Composable
private fun formatTime(state: DateChooserState.Custom): String = remember(state) {
    with(state) {
        DateTimeFormatter.ofPattern("HH:mm").format(LocalTime.of(hour, minute))
    }
}

private suspend fun getUserSelection(context: Context): LocalDateTime {
    return getDate(context).atTime(getTime(context))
}

private suspend fun getDate(context: Context, date: LocalDate = LocalDate.now()): LocalDate {
    return suspendCancellableCoroutine { cont ->
        DatePickerDialog(
            context, { _, year, month, day ->
                cont.resume(LocalDate.of(year, month + 1, day))
            }, date.year, date.monthValue - 1, date.dayOfMonth
        ).show()
    }
}

private suspend fun getTime(
    context: Context,
    time: LocalTime = LocalTime.now()
): LocalTime {
    return suspendCancellableCoroutine { cont ->
        TimePickerDialog(
            context, { _, hour, minute ->
                cont.resume(LocalTime.of(hour, minute))
            }, time.hour, time.minute, true
        ).show()
    }
}

@Preview
@Composable
private fun Preview() {
    KeepTallyTheme {
        var state by remember { mutableStateOf<DateChooserState>(DateChooserState.Now) }
        DateChooser(
            modifier = Modifier.wrapContentSize(),
            state = state,
            onStateChange = { state = it }
        )
    }
}