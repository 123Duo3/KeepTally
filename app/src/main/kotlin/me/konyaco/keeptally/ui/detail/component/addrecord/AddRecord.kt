package me.konyaco.keeptally.ui.detail.component.addrecord

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import me.konyaco.keeptally.ui.detail.component.addrecord.component.AddLabelDialog
import me.konyaco.keeptally.ui.detail.component.addrecord.component.DateChooser
import me.konyaco.keeptally.ui.detail.component.addrecord.component.DateChooserState
import me.konyaco.keeptally.ui.detail.component.addrecord.component.EditArea
import me.konyaco.keeptally.ui.detail.component.addrecord.component.EditDescription
import me.konyaco.keeptally.ui.detail.component.addrecord.component.LabelList
import me.konyaco.keeptally.ui.parseMoneyToCent
import me.konyaco.keeptally.ui.theme.KeepTallyTheme
import me.konyaco.keeptally.viewmodel.MainViewModel
import java.time.LocalDateTime

data class AddDialogState(
    val isIncomeLabel: Boolean,
    val parentLabel: String?
)

@Composable
fun AddRecord(
    modifier: Modifier,
    viewModel: MainViewModel = hiltViewModel(),
    onCloseRequest: () -> Unit
) {
    var showDialog by remember { mutableStateOf<AddDialogState?>(null) }

    var isIncome by remember { mutableStateOf(false) }
    val expenditureLabels by viewModel.expenditureLabels.collectAsState()
    val incomeLabels by viewModel.incomeLabels.collectAsState()

    val labels = if (isIncome) incomeLabels else expenditureLabels
    val primaryLabels = remember(labels) { labels.keys.map { it.label } }
    var selectedPrimaryLabel by remember(isIncome, primaryLabels) { mutableStateOf(0) }

    val secondaryLabels = remember(labels, selectedPrimaryLabel) {
        labels.keys.elementAtOrNull(selectedPrimaryLabel)
            ?.let { primary ->
                labels[primary]?.map { secondary ->
                    secondary.label
                }
            }
            ?: emptyList()
    }

    var selectedSecondaryLabel by remember(isIncome, selectedPrimaryLabel) {
        mutableStateOf<Int?>(null)
    }
    AddRecord(
        onCloseClick = onCloseRequest,
        modifier = modifier,
        isIncome = isIncome,
        onIncomeChange = { isIncome = !isIncome },
        primaryLabels = primaryLabels,
        secondaryLabels = secondaryLabels,
        checkedPrimaryLabel = selectedPrimaryLabel,
        onPrimaryLabelSelect = { selectedPrimaryLabel = it },
        checkedSecondaryLabel = selectedSecondaryLabel,
        onSecondaryLabelSelect = {
            selectedSecondaryLabel =
                if (selectedSecondaryLabel == it) null
                else it
        },
        onAddLabelClick = { isIncomeLabel, parentLabel ->
            val parentLabel = parentLabel?.let {
                labels.keys.elementAtOrNull(it)?.label
            }
            showDialog = AddDialogState(isIncomeLabel, parentLabel)
        },
        onAddRecordClick = { income, money, desc, date ->
            val primaryLabel = primaryLabels[selectedPrimaryLabel]
            val secondaryLabel = selectedSecondaryLabel?.let { secondaryLabels[it] }
            when (date) {
                is DateChooserState.Custom -> {
                    val date =
                        LocalDateTime.of(date.year, date.month, date.day, date.hour, date.minute)
                    viewModel.addRecord(income, money, primaryLabel, secondaryLabel, desc, date)
                }

                DateChooserState.Now -> {
                    viewModel.addRecord(income, money, primaryLabel, secondaryLabel, desc)
                }
            }
            onCloseRequest()
        },
    )

    showDialog?.let {
        AddLabelDialog(
            onDismissRequest = { showDialog = null },
            onConfirm = { labelName ->
                val parent = it.parentLabel
                if (parent == null) {
                    viewModel.addPrimaryLabel(labelName, it.isIncomeLabel)
                } else {
                    viewModel.addSecondaryLabel(parent, labelName, it.isIncomeLabel)
                }
                showDialog = null
            }
        )
    }
}


@Composable
fun AddRecord(
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    isIncome: Boolean,
    onIncomeChange: (Boolean) -> Unit,
    primaryLabels: List<String>,
    checkedPrimaryLabel: Int,
    onPrimaryLabelSelect: (Int) -> Unit,
    secondaryLabels: List<String>,
    checkedSecondaryLabel: Int?,
    onSecondaryLabelSelect: (Int) -> Unit,
    onAddLabelClick: (isIncomeLabel: Boolean, parentLabel: Int?) -> Unit,
    onAddRecordClick: (isIncome: Boolean, money: Int, description: String?, date: DateChooserState) -> Unit
) {
    Surface(modifier) {
        Column(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            var moneyStr by remember { mutableStateOf(TextFieldValue("")) }

            val labelColor = if (isIncome) MaterialTheme.colorScheme.tertiaryContainer
            else MaterialTheme.colorScheme.primaryContainer
            val buttonColor = if (isIncome) MaterialTheme.colorScheme.tertiary
            else MaterialTheme.colorScheme.primary

            var dateState by remember { mutableStateOf<DateChooserState>(DateChooserState.Now) }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCloseClick) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
                DateChooser(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    dateState,
                    { dateState = it }
                )
            }

            EditArea(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                income = isIncome,
                onIncomeChange = onIncomeChange,
                moneyStr = moneyStr,
                onMoneyStrChange = { moneyStr = it }
            )
            Divider(
                Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
            )
            LabelList(
                Modifier.fillMaxWidth(),
                primaryLabels,
                checkedPrimaryLabel,
                onLabelClick = onPrimaryLabelSelect,
                labelColor = labelColor,
                onAddLabelClick = { onAddLabelClick(isIncome, null) }
            )
            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            LabelList(
                Modifier.fillMaxWidth(),
                secondaryLabels,
                checkedSecondaryLabel,
                onLabelClick = onSecondaryLabelSelect,
                labelColor = labelColor,
                onAddLabelClick = { onAddLabelClick(isIncome, checkedPrimaryLabel) }
            )
            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            var input by remember { mutableStateOf(TextFieldValue()) }

            EditDescription(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = input,
                onValueChange = { input = it }
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = {
                    onAddRecordClick(
                        isIncome,
                        parseMoneyToCent(moneyStr.text),
                        input.text.takeIf { it.isNotEmpty() },
                        dateState
                    )
                    // Reset state
                    moneyStr = TextFieldValue()
                    input = TextFieldValue()
                    dateState = DateChooserState.Now
                },
                enabled = moneyStr.text.isNotBlank(),
                colors = ButtonDefaults.buttonColors(buttonColor)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Spacer(Modifier.width(12.dp))
                Text("记录")
            }
            Spacer(Modifier.height(58.dp))
        }
    }
}


@Preview
@Composable
private fun AddRecordPreview() {
    KeepTallyTheme {
        AddRecord(
            onCloseClick = {},
            isIncome = false,
            onIncomeChange = {},
            onAddRecordClick = { _, _, _, _ -> },
            primaryLabels = remember {
                listOf("购物", "餐饮", "洗浴")
            },
            secondaryLabels = remember {
                listOf("早餐", "午餐", "晚餐")
            },
            checkedPrimaryLabel = 0,
            checkedSecondaryLabel = 0,
            modifier = Modifier.fillMaxWidth(),
            onPrimaryLabelSelect = {},
            onSecondaryLabelSelect = {},
            onAddLabelClick = { _, _ -> }
        )
    }
}