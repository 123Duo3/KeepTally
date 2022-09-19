package me.konyaco.keeptally.ui.detail.component.addrecord

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import me.konyaco.keeptally.ui.RecordSign
import me.konyaco.keeptally.ui.parseMoneyToCent
import me.konyaco.keeptally.ui.theme.KeepTallyTheme
import me.konyaco.keeptally.ui.theme.RobotoSlab
import me.konyaco.keeptally.viewmodel.MainViewModel

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
        onPrimaryLabelSelect = {
            selectedPrimaryLabel = it
        },
        checkedSecondaryLabel = selectedSecondaryLabel,
        onSecondaryLabelSelect = {
            selectedSecondaryLabel = it
        },
        onAddLabelClick = { isIncomeLabel, parentLabel ->
            val parentLabel = parentLabel?.let {
                labels.keys.elementAtOrNull(it)?.label
            }
            showDialog = AddDialogState(isIncomeLabel, parentLabel)
        },
        onAddRecordClick = { income, money ->
            val primaryLabel = primaryLabels[selectedPrimaryLabel]
            val secondaryLabel = selectedSecondaryLabel?.let { secondaryLabels[it] }

            viewModel.addRecord(income, money, primaryLabel, secondaryLabel, null)
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
    onAddRecordClick: (isIncome: Boolean, money: Int) -> Unit
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

            IconButton(onClick = onCloseClick) {
                Icon(Icons.Default.Close, contentDescription = "Close")
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
            Spacer(Modifier.height(16.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = {
                    onAddRecordClick(isIncome, parseMoneyToCent(moneyStr.text))
                    moneyStr = TextFieldValue("")
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

@Composable
private fun EditArea(
    modifier: Modifier,
    income: Boolean,
    onIncomeChange: (Boolean) -> Unit,
    moneyStr: TextFieldValue,
    onMoneyStrChange: (TextFieldValue) -> Unit
) {
    val color = if (income) MaterialTheme.colorScheme.tertiary
    else MaterialTheme.colorScheme.primary
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onIncomeChange(!income) }) {
            Text(
                modifier = Modifier.offset(y = (-2).dp),
                text = if (income) RecordSign.POSITIVE else RecordSign.NEGATIVE,
                style = MaterialTheme.typography.displaySmall,
                color = color,
                fontFamily = FontFamily.RobotoSlab
            )
        }
        Divider(Modifier.size(1.dp, 32.dp))
//        var field by remember(moneyStr) { mutableStateOf(TextFieldValue(moneyStr)) }
        val focus = LocalFocusManager.current
        BasicTextField(
            modifier = Modifier
                .weight(1f)
                .alignByBaseline(),
            value = moneyStr,
            onValueChange = {
                if (validateNumberString(it.text)) {
                    val text = normalizeNumberString(it)
                    onMoneyStrChange(text)
                }
            },
            textStyle = MaterialTheme.typography.displaySmall.copy(
                fontFamily = FontFamily.RobotoSlab,
                color = color,
                textAlign = TextAlign.End
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focus.clearFocus()
            }),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
        )

        Spacer(Modifier.width(8.dp))
        Text(
            modifier = Modifier.alignByBaseline(),
            text = RecordSign.RMB,
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = FontFamily.RobotoSlab
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EditAreaPreview() {
    KeepTallyTheme {
        EditArea(
            Modifier.fillMaxWidth(),
            true,
            {},
            remember { TextFieldValue() },
            onMoneyStrChange = {})
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LabelList(
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
            itemsIndexed(labels) { index, item ->
                LabelItem(
                    modifier = Modifier.animateItemPlacement(),
                    selected = checkedLabel == index,
                    onSelectChange = { onLabelClick(index) },
                    text = item,
                    activeColor = labelColor
                )
            }
            item {
                LabelItem(
                    modifier = Modifier.animateItemPlacement(),
                    selected = false,
                    onSelectChange = { onAddLabelClick() },
                    text = "＋",
                    activeColor = labelColor
                )
            }
        }
        Divider(
            Modifier
                .height(48.dp)
                .width(1.dp)
        )
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
        var enabledLabel by remember { mutableStateOf(0) }
        LabelList(modifier = Modifier.fillMaxWidth(), primaryLabels, enabledLabel, onLabelClick = {
            enabledLabel = it
        }, labelColor = MaterialTheme.colorScheme.tertiary, onAddLabelClick = {})
    }
}

@Composable
private fun LabelContainer(
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

@Composable
private fun LabelItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onSelectChange: (Boolean) -> Unit,
    text: String,
    activeColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    activeContentColor: Color = contentColorFor(activeColor)
) {
    LabelContainer(
        modifier = modifier,
        selected = selected,
        onSelectChange = onSelectChange,
        activeColor = activeColor,
        activeContentColor = activeContentColor
    ) {
        Box(Modifier.padding(12.dp, 6.dp), Alignment.Center) {
            Text(
                modifier = Modifier.wrapContentSize(),
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Preview
@Composable
private fun PreviewLabel() {
    KeepTallyTheme {
        var selected by remember { mutableStateOf(false) }
        LabelItem(selected = selected, onSelectChange = { selected = it }, text = "标签")
    }
}

@Composable
fun AddLabel(
    activeColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    activeContentColor: Color = contentColorFor(activeColor)
) {
    var selected by remember { mutableStateOf(false) }

//    val focus = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }

    LabelContainer(
        modifier = Modifier.wrapContentSize(),
        selected = selected,
        onSelectChange = { focusRequester.requestFocus() },
        activeColor = activeColor,
        activeContentColor = activeContentColor,
        interactionSource = interactionSource
    ) {
        Box(Modifier.padding(horizontal = 12.dp), Alignment.Center) {
            var value by remember { mutableStateOf("") }
            BasicTextField(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(vertical = 6.dp)
                    .focusRequester(focusRequester)
                    .onFocusChanged { selected = it.isFocused },
                value = value,
                interactionSource = interactionSource,
                onValueChange = { value = it },
                textStyle = MaterialTheme.typography.labelLarge.copy(
                    color = LocalContentColor.current
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusRequester.freeFocus()
//                    focus.clearFocus()
                    // TODO: On add label
                }),
                cursorBrush = SolidColor(LocalContentColor.current)
            )
            if (!selected) Icon(Icons.Sharp.Add, contentDescription = "Add label")
        }
    }
}

@Preview
@Composable
fun AddLabelPreview() {
    KeepTallyTheme {
        AddLabel()
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
            onAddRecordClick = { _, _ -> },
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