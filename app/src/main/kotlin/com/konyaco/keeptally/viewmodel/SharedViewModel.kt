package com.konyaco.keeptally.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.konyaco.keeptally.storage.database.AppDatabase
import com.konyaco.keeptally.storage.entity.RecordType
import com.konyaco.keeptally.viewmodel.model.DateRange
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedViewModel @Inject constructor(
    private val database: AppDatabase
) : ViewModel() {
    val dateRange = mutableStateOf<DateRange>(DateRange.Month.now())
    val colors = mutableStateOf<Map<Int, Int>>(emptyMap())
    val isReady = MutableStateFlow(false)

    private val lock = Mutex()

    init {
        viewModelScope.launch(Dispatchers.Main) {
            prepopulateData()
            loadColors()
            isReady.value = true
        }
    }

    private val preExp = mapOf(
        "餐饮" to listOf("早餐", "午餐", "晚餐", "小吃", "饮料"),
        "购物" to listOf("日用品"),
        "交通" to listOf("公交地铁", "骑行"),
        "生活" to listOf("电费", "水费", "房租"),
        "娱乐" to emptyList()
    )

    private val preIncome = mapOf<String, List<String>>(
        "工资" to emptyList(),
        "生活费" to emptyList()
    )

    private suspend fun prepopulateData() {
        val recordTypeDao = database.recordTypeDao()
        if (recordTypeDao.count() == 0L) {

            preExp.forEach { (k, v) ->
                val id = recordTypeDao.insertAll(RecordType(0, k, null, false))[0].toInt()
                recordTypeDao.insertAll(*v.map {
                    RecordType(0, it, id, false)
                }.toTypedArray())
            }

            preIncome.forEach { (k, v) ->
                val id = recordTypeDao.insertAll(RecordType(0, k, null, true))[0].toInt()
                recordTypeDao.insertAll(*v.map {
                    RecordType(0, it, id, true)
                }.toTypedArray())
            }
        }
    }

    private suspend fun loadColors() = lock.withLock {
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
        colors.value = map
    }

    suspend fun refresh() {
        loadColors()
    }

    suspend fun sync() {

    }
}