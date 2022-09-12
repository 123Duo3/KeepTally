package me.konyaco.keeptally.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.konyaco.keeptally.storage.database.AppDatabase
import me.konyaco.keeptally.viewmodel.model.DateRange
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedViewModel @Inject constructor(
    private val database: AppDatabase
) : ViewModel() {
    val dateRange = MutableStateFlow<DateRange>(DateRange.Month.now())
    val colors: MutableStateFlow<Map<Int, Int>> = MutableStateFlow(emptyMap())

    private val lock = Mutex()

    init {
        viewModelScope.launch(Dispatchers.IO) { load() }
    }

    suspend fun load() = lock.withLock {
        val map = mutableMapOf<Int, Int>()
        var incomeI = 0
        var expI = 0

        database.recordTypeDao().getAllRoot().forEach { recordType ->
            if (recordType.isIncome) {
                map[recordType.id] = incomeI++
            } else {
                map[recordType.id] = expI++
            }
        }
        colors.emit(map)
    }
}