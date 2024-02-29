package com.konyaco.keeptally.viewmodel

import android.util.Log
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konyaco.keeptally.service.KeepTallyService
import com.konyaco.keeptally.storage.MyDataStore
import com.konyaco.keeptally.storage.SnowFlakeIDGenerator
import com.konyaco.keeptally.storage.database.AppDatabase
import com.konyaco.keeptally.storage.entity.RecordType
import com.konyaco.keeptally.ui.statistic.component.TabItem
import com.konyaco.keeptally.viewmodel.model.DateRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.descriptors.serialDescriptor
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "SharedViewModel"

@Singleton
class SharedViewModel @Inject constructor(
    private val database: AppDatabase,
    private val myDataStore: MyDataStore,
    private val keepTallyService: KeepTallyService
) : ViewModel() {
    val dateRange = MutableStateFlow<DateRange>(DateRange.Month.now())
    val colors = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val isReady = MutableStateFlow(false)
    val syncState = MutableStateFlow<SyncState>(SyncState.Synced)
    val snackbarHostState = SnackbarHostState()
    val loginState = MutableStateFlow(LoginState.Loading)

    sealed class SyncState {
        object Syncing: SyncState()
        object Synced: SyncState()
        data class Failed(
            val message: String
        ): SyncState()
    }

    sealed class LoginState {
        object Loading: LoginState()
        object NotLogin: LoginState()
        data class LoggedIn(
            val profile: UserProfile
        ): LoginState()
    }

    data class UserProfile(
        val username: String,
        val avatarUrl: String,
        val email: String,
        val bio: String,
    )

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
        syncState.value = SyncState.Syncing
        try {
            keepTallyService.sync()
            syncState.value = SyncState.Synced
        } catch (e: Exception) {
            Log.e(TAG, "同步失败", e)
            syncState.value = SyncState.Failed(e.message ?: "未知错误")
            launch {
                snackbarHostState.showSnackbar("同步失败：${e.message}")
            }
        }
    }
}