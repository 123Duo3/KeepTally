package me.konyaco.keeptally.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.konyaco.keeptally.storage.database.AppDatabase
import zonedEpoch
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(
    private val sharedViewModel: SharedViewModel,
    private val database: AppDatabase
) : ViewModel() {

    // TODO: 2022/9/11
    data class Summary(
        val expenditure: Int,
        val income: Int,
        val balance: Int,
        val budget: Int
    )

    val summary: MutableStateFlow<Summary> = MutableStateFlow(Summary(0, 0, 0, 0))

    init {
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch {
            val range = sharedViewModel.dateRange.value

            val records = database.recordDao().loadAllByDate(
                range.start.zonedEpoch(),
                range.end.zonedEpoch()
            )
            var incomeSum = 0
            var expenditureSum = 0

            records.forEach {
                if (it.money >= 0) {
                    expenditureSum += it.money
                } else {
                    incomeSum += it.money
                }
            }
            summary.emit(Summary(incomeSum, expenditureSum, incomeSum + expenditureSum, 0))
        }
    }
}