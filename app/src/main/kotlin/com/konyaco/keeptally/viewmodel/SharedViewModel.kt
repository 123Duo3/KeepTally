package com.konyaco.keeptally.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konyaco.keeptally.storage.MyDataStore
import com.konyaco.keeptally.storage.SnowFlakeIDGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.konyaco.keeptally.storage.database.AppDatabase
import com.konyaco.keeptally.storage.entity.RecordType
import com.konyaco.keeptally.viewmodel.model.DateRange
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedViewModel @Inject constructor(
    private val database: AppDatabase,
    private val myDataStore: MyDataStore
) : ViewModel() {
    val dateRange = mutableStateOf<DateRange>(DateRange.Month.now())
    val colors = mutableStateOf<Map<Long, Int>>(emptyMap())
    val isReady = MutableStateFlow(false)
    val isSyncing = mutableStateOf(false)

    private val lock = Mutex()

    init {
        viewModelScope.launch(Dispatchers.Main) {
            coroutineScope {
                launch { prepopulateData() }
                launch { loadColors() }
                launch { loadLoginStatus() }
                joinAll()
            }
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

    private suspend fun loadLoginStatus() {

    }

    private suspend fun prepopulateData() {
        val recordTypeDao = database.recordTypeDao()
        if (recordTypeDao.count() == 0L) {

            preExp.forEach { (k, v) ->
                val parentId = SnowFlakeIDGenerator.nextId()
                recordTypeDao.insertAll(RecordType(parentId, k, null, false))[0]
                recordTypeDao.insertAll(*v.map {
                    RecordType(SnowFlakeIDGenerator.nextId(), it, parentId, false)
                }.toTypedArray())
            }

            preIncome.forEach { (k, v) ->
                val parentId = SnowFlakeIDGenerator.nextId()
                recordTypeDao.insertAll(RecordType(parentId, k, null, true))[0]
                recordTypeDao.insertAll(*v.map {
                    RecordType(SnowFlakeIDGenerator.nextId(), it, parentId, true)
                }.toTypedArray())
            }
        }
    }

    private suspend fun loadColors() = lock.withLock {
        val map = mutableMapOf<Long, Int>()
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

    fun sync() = viewModelScope.launch(Dispatchers.IO) {
        isSyncing.value = true
        try {
            delay(3000)
        } finally {
            isSyncing.value = false
        }
    }
}