package com.konyaco.keeptally.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.konyaco.keeptally.storage.database.AppDatabase
import com.konyaco.keeptally.viewmodel.model.DateRange
import com.konyaco.keeptally.viewmodel.model.Money
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

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appDatabase: AppDatabase,
    private val sharedViewModel: SharedViewModel
) : ViewModel() {
    private val recordDao = appDatabase.recordDao()
    private val recordTypeDao = appDatabase.recordTypeDao()
    private val localZoneId = ZoneId.systemDefault()

    val state = mutableStateOf<State>(State.Initializing)
    val records = mutableStateOf<List<DailyRecord>>(emptyList())
    val expenditureLabels = mutableStateOf<Map<RecordType, List<RecordType>>>(emptyMap())
    val incomeLabels = mutableStateOf<Map<RecordType, List<RecordType>>>(emptyMap())
    val dateRange = sharedViewModel.dateRange
    val statistics = mutableStateOf<Statistics>(Statistics(Money(0), Money(0), Money(0)))

    sealed class State {
        object Initializing: State()
        object Loading: State()
        object Done: State()
    }

    data class DailyRecord(
        val date: Date,
        val expenditure: Money,
        val income: Money,
        val records: List<Record>,
    )

    data class Record(
        val id: Int,
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

    init {
        viewModelScope.launch(Dispatchers.IO) {
            sharedViewModel.isReady.collect {
                if (it) init()
            }
        }
    }

    private val hhmFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val dateFormatter = DateTimeFormatter.ofPattern("MMMd" + "日")

    private suspend fun init() {
        refreshLabels()
        refreshRecords()
        state.value = State.Done
    }

    private suspend fun refreshRecords() = withContext(Dispatchers.IO) {
        val range = dateRange.value
        val start = range.start.zonedEpoch()
        val end = range.end.zonedEpoch()

        var expenditure = 0
        var income = 0

        val result = recordDao.loadAllByDateDesc(start, end).map {
            it.mapToRecord().also {
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

    private suspend fun EntityRecord.mapToRecord(): Record {
        val instant = Instant.ofEpochSecond(this.timestamp)
        val zoned = instant.atZone(localZoneId)
        val time = hhmFormatter.format(zoned)
        val date = Date(dateFormatter.format(zoned), calculateDayOffset(zoned))
        val label = recordTypeDao.loadAllByIds(this.typeId).first().mapToRecordType()

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

    private suspend fun refreshLabels() = withContext(Dispatchers.IO) {
        val expMap = mutableMapOf<RecordType, List<RecordType>>()
        val incomeMap = mutableMapOf<RecordType, List<RecordType>>()

        recordTypeDao.getAllRoot().forEach {
            val key = it.mapToRecordType()
            val list = recordTypeDao.getSubTypes(it.id).map { it.mapToRecordType() }
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
            // Check existence
            if (recordTypeDao.getRootByLabel(name) == null) {
                recordTypeDao.insertAll(EntityRecordType(0, name, null, isIncomeLabel))
                sharedViewModel.refresh()
                refreshLabels()
            } else {
                // TODO: Show error message to user
                error("Duplicated label name")
            }
        }
    }

    fun addSecondaryLabel(primaryLabel: String, name: String, isIncomeLabel: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val primary = recordTypeDao.getRootByLabel(primaryLabel)

            // Check existence
            if (primary != null && primary.isIncome == isIncomeLabel) {
                val exist =
                    recordTypeDao.getSubTypes(primary.id).firstOrNull { it.label == name } != null

                if (!exist) {
                    recordTypeDao.insertAll(EntityRecordType(0, name, primary.id, isIncomeLabel))
                    refreshLabels()
                } else {
                    // TODO: Show error message to user
                    error("The label was existed")
                }
            } else {
                // TODO: Show error message to user
                error("Primary label was not found")
            }
        }
    }

    fun addRecord(
        isIncome: Boolean,
        money: Int,
        primaryLabel: String,
        secondaryLabel: String?,
        description: String?
    ) {
        addRecord(isIncome, money, primaryLabel, secondaryLabel, description, LocalDateTime.now())
    }

    fun addRecord(
        isIncome: Boolean,
        money: Int,
        primaryLabel: String,
        secondaryLabel: String?,
        description: String?,
        date: LocalDateTime
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val primary = recordTypeDao.getRootByLabel(primaryLabel)
                ?: error("Primary label: $primaryLabel was not found")
            val label = if (secondaryLabel != null) {
                recordTypeDao.getSubTypes(primary.id)
                    .firstOrNull { it.label == secondaryLabel }
                    ?: error("Secondary label $secondaryLabel in $primaryLabel was not found")
            } else primary

            val money = if (isIncome) abs(money) else -abs(money)
            recordDao.insertAll(
                EntityRecord(
                    0,
                    money,
                    date.atZone(ZoneId.systemDefault()).toEpochSecond(),
                    label.id,
                    description
                )
            )
            refreshRecords()
        }
    }

    private fun groupToDailyRecord(records: List<Record>): List<DailyRecord> {
        return records.groupBy {
            it.date.dateString
        }.map { (date, records) ->
            var expenditure = 0
            var income = 0
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

    private suspend fun EntityRecordType.mapToRecordType(): RecordType {
        val parent = parentId?.let { recordTypeDao.loadAllByIds(it).firstOrNull() }
        return RecordType(
            label,
            parent?.label,
            isIncome,
            sharedViewModel.colors.value[parentId ?: id]!!
        )
    }

    fun deleteRecord(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            recordDao.delete(com.konyaco.keeptally.storage.entity.Record(id = id, 0, 0, 0, null))
            refreshRecords()
        }
    }
}

private fun Pair<String, String>.joinToString(): Triple<String, String, String> {
    return Triple(first, second, "$first.$second")
}