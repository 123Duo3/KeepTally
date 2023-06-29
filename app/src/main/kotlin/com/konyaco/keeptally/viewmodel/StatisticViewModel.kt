package com.konyaco.keeptally.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.konyaco.keeptally.storage.database.AppDatabase
import com.konyaco.keeptally.viewmodel.model.DateRange
import com.konyaco.keeptally.viewmodel.model.Money
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
        val expenditure: Money,
        val income: Money,
        val balance: Money,
        val budget: Money
    )

    data class Expenditure(
        val typeId: Int,
        val label: String,
        val money: Money,
        val budget: Money,
        val color: Int
    )

    data class Income(
        val typeId: Int,
        val label: String,
        val money: Money,
        val color: Int
    )

    private val defaultMoney = Money(0)

    val summary =
        mutableStateOf<Summary>(Summary(defaultMoney, defaultMoney, defaultMoney, defaultMoney))
    val expenditures = mutableStateOf<List<Expenditure>>(emptyList())
    val incomes = mutableStateOf<List<Income>>(emptyList())

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            refresh(sharedViewModel.dateRange.value)
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
            // Get the root label
            while (type.parentId != null) {
                type = types[type.parentId!!]!!
            }
            if (type.isIncome) {
                val income = incomesR.getOrPut(type.id) {
                    Income(
                        type.id, type.label, Money(0),
                        sharedViewModel.colors.value[type.id]!!
                    )
                }
                incomesR[type.id] =
                    income.copy(money = Money(income.money.money + record.money))
            } else {
                val exp = expenditureR.getOrPut(type.id) {
                    Expenditure(
                        type.id, type.label, Money(0), Money(0),
                        sharedViewModel.colors.value[type.id]!!
                    )
                }
                expenditureR[type.id] =
                    exp.copy(money = Money(exp.money.money + record.money))
            }
        }

        val incomeSum = incomesR.values.sumOf { it.money.money }
        val expenditureSum = expenditureR.values.sumOf { it.money.money }

        expenditures.value = expenditureR.values.toList()
        incomes.value = incomesR.values.toList()
        summary.value =
            Summary(
                Money(expenditureSum),
                Money(incomeSum),
                Money(incomeSum + expenditureSum),
                Money(0) // TODO
            )
    }
}