package me.konyaco.keeptally.ui.detail

import androidx.compose.material.*
import androidx.compose.runtime.*
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.sync.Mutex

@Composable
fun AddLabelDialog(onDismissRequest: () -> Unit, onConfirm: (String) -> Unit) {
    var value by remember { mutableStateOf("") }
    AlertDialog(
        title = { Text(text = "新建标签") },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = { onConfirm(value) }) {
                Text("确定")
            }
        }, dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("取消")
            }
        }
    )
}

@Composable
fun AddLabelDialog(state: AddLabelDialogState) {

}

class AddLabelDialogState {
    private var continuation: CancellableContinuation<String>? = null
    private val mutex = Mutex()

    interface AddLabelDialogData

    suspend fun showAndGetResult(parentLabel: String): String {
        TODO()
    }
}