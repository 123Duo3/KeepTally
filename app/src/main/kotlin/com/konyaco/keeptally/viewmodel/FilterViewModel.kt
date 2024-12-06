package com.konyaco.keeptally.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konyaco.keeptally.service.KeepTallyService
import com.konyaco.keeptally.storage.entity.RecordType
import com.konyaco.keeptally.viewmodel.MainViewModel.Companion.mapToRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val sharedViewModel: SharedViewModel,
    private val keepTallyService: KeepTallyService,
) : ViewModel() {
    val isIncome = mutableStateOf(false)

    data class Label(
        val id: Long,
        val label: String,
        val isIncome: Boolean
    )

    val primaryTypes = mutableStateOf(emptyList<Label>())
    val currentPrimaryType = mutableStateOf<Label?>(null)
    val containedPrimaryTypes = mutableStateOf(setOf<Label>())

    val secondaryTypes = mutableStateOf(emptyList<Label>())
    val selectedPrimaryTypes = mutableStateOf(setOf<Label>())
    val selectedSecondaryTypes = mutableStateOf(setOf<Label>())

    val records = mutableStateOf(emptyList<MainViewModel.DailyRecord>())

    private var mPrimaryTypes = emptyList<RecordType>()

    init {
        created()
    }

    fun created() {
        getTypes()
        getRecords()
    }

    fun mounted() {
        getTypes()
        getRecords()
    }

    fun changeType(isIncome: Boolean) {
        this.isIncome.value = isIncome
        getTypes()
    }

    fun selectPrimaryType(label: Label) {
        currentPrimaryType.value = label
        viewModelScope.launch {
            keepTallyService.getSecondaryTypesByPrimaryId(
                mPrimaryTypes.find { it.id == label.id }!!.id
            ).let {
                secondaryTypes.value = it.map {
                    Label(id = it.id, label = it.label, isIncome = it.isIncome)
                }
            }
        }
    }

    fun selectSecondaryType(label: Label, selected: Boolean) {
        if (selected) {
            selectedSecondaryTypes.value += label
        } else {
            selectedSecondaryTypes.value -= label
        }
        conditionContained()
        getRecords()
    }

    /**
     * 全选/取消全选当前的二级分类
     */
    fun toggleSelectAll() {
        if (currentPrimaryType.value == null) return
        val selectedAll = selectedSecondaryTypes.value.containsAll(secondaryTypes.value)
        if (selectedAll) {
            selectedSecondaryTypes.value -= secondaryTypes.value
        } else {
            selectedSecondaryTypes.value += secondaryTypes.value
        }
        conditionContained()
        getRecords()
    }

    private fun getTypes() {
        viewModelScope.launch {
            mPrimaryTypes = keepTallyService.getPrimaryTypes()

            primaryTypes.value = if (isIncome.value) {
                mPrimaryTypes.filter { it.isIncome }
            } else {
                mPrimaryTypes.filter { !it.isIncome }
            }.map {
                Label(id = it.id, label = it.label, isIncome = it.isIncome)
            }
        }
    }

    /**
     * 如果选择了任何二级类型，那么一级类型被选中，否则取消选中
     */
    private fun conditionContained() {
        val currentPrimaryType = currentPrimaryType.value!!

        val containsAny = secondaryTypes.value.any { selectedSecondaryTypes.value.contains(it) }
        if (containsAny) {
            containedPrimaryTypes.value += currentPrimaryType
        } else {
            containedPrimaryTypes.value -= currentPrimaryType
        }

        val containsAll = selectedSecondaryTypes.value.containsAll(secondaryTypes.value)
        if (containsAll) {
            selectedPrimaryTypes.value += currentPrimaryType
        } else {
            selectedPrimaryTypes.value -= currentPrimaryType
        }
    }

    private fun getRecords() {
        viewModelScope.launch {
            val start = sharedViewModel.dateRange.value.start
            val end = sharedViewModel.dateRange.value.end

            // 将一级和二级类型合并后查询
            val types = selectedPrimaryTypes.value + selectedSecondaryTypes.value
            val typeIds = keepTallyService.getRecordsByLabels(types.map { it.label }.toList()).map { it.id }
            val records = keepTallyService.getRecordsByDateRangeAndTypes(start, end, typeIds).map {
                it.mapToRecord(keepTallyService, sharedViewModel).also {
                    if (it.money.money < 0) {
//                        expenditure += abs(it.money.money)
                    } else {
//                        income += it.money.money
                    }
                }
            }
            val dailyRecords = MainViewModel.groupToDailyRecord(records)
            this@FilterViewModel.records.value = dailyRecords
        }
    }
}