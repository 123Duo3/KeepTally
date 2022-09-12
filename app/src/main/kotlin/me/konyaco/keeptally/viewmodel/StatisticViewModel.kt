package me.konyaco.keeptally.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.konyaco.keeptally.storage.database.AppDatabase
import me.konyaco.keeptally.viewmodel.model.DateRange
import zonedEpoch
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(
    private val sharedViewModel: SharedViewModel,
    private val database: AppDatabase
) : ViewModel() {

    private val recordDao = database.recordDao()
    private val recordTypeDao = database.recordTypeDao()

    // TODO: 2022/9/11
    data class Summary(
        val expenditure: Int,
        val income: Int,
        val balance: Int,
        val budget: Int
    )

    data class Expenditure(
        val typeId: Int,
        val label: String,
        val money: Int,
        val budget: Int,
        val color: Int
    )

    data class Income(
        val typeId: Int,
        val label: String,
        val money: Int,
        val color: Int
    )

    val summary: MutableStateFlow<Summary> = MutableStateFlow(Summary(0, 0, 0, 0))
    val expenditures: MutableStateFlow<List<Expenditure>> = MutableStateFlow(emptyList())
    val incomes: MutableStateFlow<List<Income>> = MutableStateFlow(emptyList())

    init {
        viewModelScope.launch {
            sharedViewModel.dateRange.collectLatest {
                refresh(it)
            }
        }
    }

    private suspend fun refresh(range: DateRange) {
        val records = recordDao.loadAllByDate(
            range.start.zonedEpoch(),
            range.end.zonedEpoch()
        )
        val types = recordTypeDao.getAll().let {
            buildMap {
                it.forEach { put(it.id, it) }
            }
        }

        val incomesR = mutableMapOf<Int, Income>()
        val expenditureR = mutableMapOf<Int, Expenditure>()

        // TODO(Optimize): Use flow
        records.forEach { record ->
            // TODO(Optimize): Add cache to optimize
            var type = types[record.typeId]!!
            while (type.parentId != null) {
                type = types[type.parentId!!]!!
            }
            if (type.isIncome) {
                val income = incomesR.getOrPut(type.id) {
                    Income(type.id, type.label, 0, sharedViewModel.colors.value[type.id]!!)
                }
                incomesR[type.id] = income.copy(money = income.money + record.money)
            } else {
                val exp = expenditureR.getOrPut(type.id) {
                    Expenditure(type.id, type.label, 0, 0, sharedViewModel.colors.value[type.id]!!)
                }
                expenditureR[type.id] = exp.copy(money = exp.money + record.money)
            }
        }

        val incomeSum = incomesR.values.sumOf { it.money }
        val expenditureSum = expenditureR.values.sumOf { it.money }

        expenditures.emit(expenditureR.values.toList())
        incomes.emit(incomesR.values.toList())
        summary.emit(Summary(expenditureSum, incomeSum, incomeSum + expenditureSum, 0))
    }
}