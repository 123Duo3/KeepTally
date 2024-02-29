package com.konyaco.keeptally.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konyaco.keeptally.service.KeepTallyService
import com.konyaco.keeptally.ui.component.HomeTopBarState
import com.konyaco.keeptally.viewmodel.model.DateRange
import com.konyaco.keeptally.viewmodel.model.Money
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.math.abs
import com.konyaco.keeptally.storage.entity.Record as EntityRecord
import com.konyaco.keeptally.storage.entity.RecordType as EntityRecordType

private const val TAG = "MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sharedViewModel: SharedViewModel,
    private val keepTallyService: KeepTallyService,
) : ViewModel() {

    val state = mutableStateOf<State>(State.Initializing)
    val records = mutableStateOf<List<DailyRecord>>(emptyList())
    val expenditureLabels = mutableStateOf<Map<RecordType, List<RecordType>>>(emptyMap())
    val incomeLabels = mutableStateOf<Map<RecordType, List<RecordType>>>(emptyMap())
    val dateRange = sharedViewModel.dateRange
    val statistics = mutableStateOf<Statistics>(Statistics(Money(0), Money(0), Money(0)))
    val homeTopBarState: HomeTopBarState = HomeTopBarState(HomeTopBarState.TabItem.Detail)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            sharedViewModel.isReady.collect {
                if (it) init()
            }
        }
    }

    fun addRecord(
        isIncome: Boolean,
        money: Long,
        primaryLabel: String,
        secondaryLabel: String?,
        description: String?
    ) {
        addRecord(isIncome, money, primaryLabel, secondaryLabel, description, LocalDateTime.now())
    }

    fun addRecord(
        isIncome: Boolean,
        money: Long,
        primaryLabel: String,
        secondaryLabel: String?,
        description: String?,
        date: LocalDateTime
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            keepTallyService.addRecord(
                isIncome = isIncome,
                money = money,
                primaryLabel = primaryLabel,
                secondaryLabel = secondaryLabel,
                description = description,
                date = date
            )
            sharedViewModel.sync()
            refreshRecords()
        }
    }

    fun deleteRecord(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            keepTallyService.deleteRecord(id)
            sharedViewModel.sync()
            refreshRecords()
        }
    }

    private suspend fun init() {
        refreshLabels()
        refreshRecords()
        state.value = State.Done
    }

    private suspend fun refreshRecords() = withContext(Dispatchers.IO) {
        val range = dateRange.value
        var expenditure = 0L
        var income = 0L

        val result = keepTallyService.getRecordsByDateRange(range.start, range.end).map {
            it.mapToRecord(keepTallyService, sharedViewModel).also {
                if (it.money.money < 0) {
                    expenditure += abs(it.money.money)
                } else {
                    income += it.money.money
                }
            }
        }
        statistics.value = Statistics(Money(expenditure), Money(income), Money(0)) // TODO: Budget
        val dailyRecords = groupToDailyRecord(result)
        records.value = dailyRecords
    }

    private suspend fun refreshLabels() = withContext(Dispatchers.IO) {
        val expMap = mutableMapOf<RecordType, List<RecordType>>()
        val incomeMap = mutableMapOf<RecordType, List<RecordType>>()

        keepTallyService.getPrimaryTypes().forEach {
            val key = it.mapToRecordType(keepTallyService, sharedViewModel)
            val list = keepTallyService.getSecondaryTypesByPrimaryId(it.id).map {
                it.mapToRecordType(keepTallyService, sharedViewModel)
            }
            if (it.isIncome) {
                incomeMap[key] = list
            } else {
                expMap[key] = list
            }
        }

        expenditureLabels.value = expMap
        incomeLabels.value = incomeMap
    }

    fun setDateRange(dateRange: DateRange) {
        this.dateRange.value = dateRange
        viewModelScope.launch(Dispatchers.IO) {
            refreshRecords()
        }
    }

    fun addPrimaryLabel(name: String, isIncomeLabel: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                keepTallyService.addPrimaryLabel(name, isIncomeLabel)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add primary label", e)
                sharedViewModel.snackbarHostState.showSnackbar("添加失败")
            }
            sharedViewModel.refresh()
            refreshLabels()
        }
    }

    fun addSecondaryLabel(primaryLabel: String, name: String, isIncomeLabel: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                keepTallyService.addSecondaryType(primaryLabel, name, isIncomeLabel)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add secondary label", e)
                sharedViewModel.snackbarHostState.showSnackbar("添加失败")
            }
            sharedViewModel.refresh()
            refreshLabels()
        }
    }

    companion object {
        sealed class State {
            object Initializing : State()
            object Loading : State()
            object Done : State()
        }

        data class DailyRecord(
            val date: Date,
            val expenditure: Money,
            val income: Money,
            val records: List<Record>,
        )

        data class Record(
            val id: Long,
            val isIncome: Boolean,
            val money: Money,
            val date: Date,
            val time: String,
            val type: RecordType,
            val description: String?
        )

        data class Date(
            val dateString: String,
            val daysOffset: Int
        )

        data class RecordType(
            val label: String,
            val parent: String?,
            /**
             * Income label or expenditure label
             */
            val income: Boolean,
            val colorIndex: Int
        )

        data class Statistics(
            val expenditure: Money,
            val income: Money,
            val budget: Money,
        )

        fun groupToDailyRecord(records: List<Record>): List<DailyRecord> {
            return records.groupBy {
                it.date.dateString
            }.map { (date, records) ->
                var expenditure = 0L
                var income = 0L
                records.forEach {
                    if (it.money.money < 0) {
                        expenditure += abs(it.money.money)
                    } else {
                        income += it.money.money
                    }
                }
                DailyRecord(
                    date = records.first().date,
                    expenditure = Money(expenditure),
                    income = Money(income),
                    records = records
                )
            }
        }

        suspend fun EntityRecordType.mapToRecordType(
            keepTallyService: KeepTallyService,
            sharedViewModel: SharedViewModel
        ): RecordType {
            val parent = parentId?.let { keepTallyService.getTypesByIds(it).firstOrNull() }
            return RecordType(
                label,
                parent?.label,
                isIncome,
                sharedViewModel.colors.value[parentId ?: id]!!
            )
        }

        private val hhmFormatter = DateTimeFormatter.ofPattern("HH:mm")
        private val dateFormatter = DateTimeFormatter.ofPattern("MMMd" + "日")
        private val localZoneId = ZoneId.systemDefault()

        suspend fun EntityRecord.mapToRecord(
            keepTallyService: KeepTallyService,
            sharedViewModel: SharedViewModel
        ): Record {
            val instant = Instant.ofEpochSecond(this.timestamp)
            val zoned = instant.atZone(localZoneId)
            val time = hhmFormatter.format(zoned)
            val date = Date(
                dateFormatter.format(zoned),
                calculateDayOffset(zoned)
            )
            val label = keepTallyService.getTypesByIds(this.typeId).first()
                .mapToRecordType(keepTallyService, sharedViewModel)

            return Record(
                money = Money(this.money),
                isIncome = false,
                date = date,
                time = time,
                type = label,
                id = this.id,
                description = description
            )
        }

        private fun calculateDayOffset(date: ZonedDateTime): Int {
            val today = with(ZonedDateTime.now()) {
                ZonedDateTime.of(year, monthValue, dayOfMonth, 0, 0, 0, 0, zone)
            }
            val target = with(date) {
                ZonedDateTime.of(year, monthValue, dayOfMonth, 0, 0, 0, 0, zone)
            }
            val offsetDay = Duration.between(target, today).toDays().toInt()
            return offsetDay
        }
    }
}

private fun Pair<String, String>.joinToString(): Triple<String, String, String> {
    return Triple(first, second, "$first.$second")
}