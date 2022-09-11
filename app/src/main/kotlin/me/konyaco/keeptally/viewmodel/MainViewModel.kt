package me.konyaco.keeptally.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.konyaco.keeptally.storage.database.AppDatabase
import zonedEpoch
import java.time.*
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.math.abs
import me.konyaco.keeptally.storage.entity.Record as EntityRecord
import me.konyaco.keeptally.storage.entity.RecordType as EntityRecordType

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appDatabase: AppDatabase,
    private val sharedViewModel: SharedViewModel
) : ViewModel() {
    private val recordDao = appDatabase.recordDao()
    private val recordTypeDao = appDatabase.recordTypeDao()

    private val localZoneId = ZoneId.systemDefault()

    data class DailyRecord(
        val date: Date,
        val expenditure: Int,
        val income: Int,
        val records: List<Record>
    )

    data class Record(
        val id: Int,
        val money: Int,
        val date: Date,
        val time: String,
        val type: RecordType
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
        val income: Boolean
    )

    val records = MutableStateFlow<List<DailyRecord>>(emptyList())

    val expenditureLabels = MutableStateFlow<Map<RecordType, List<RecordType>>>(emptyMap())
    val incomeLabels = MutableStateFlow<Map<RecordType, List<RecordType>>>(emptyMap())

    val dateRange = sharedViewModel.dateRange

    val statistics = MutableStateFlow<Statistics>(Statistics(0, 0, 0))

    data class Statistics(
        val expenditure: Int,
        val income: Int,
        val budget: Int
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            refreshLabels()
            refreshRecords()
        }
    }

    sealed class DateRange(val start: LocalDate, val end: LocalDate) {
        data class Month(val year: Int, val month: Int) :
            DateRange(
                LocalDate.of(year, month, 1),
                LocalDate.of(year, month, 1).plusMonths(1)
            ) {
            companion object {
                fun now(): Month {
                    val dateTime = LocalDateTime.now()
                    return Month(dateTime.year, dateTime.monthValue)
                }
            }
        }

        data class Day(val year: Int, val month: Int, val day: Int) : DateRange(
            LocalDate.of(year, month, day),
            LocalDate.of(year, month, day).plusDays(1)
        ) {
            companion object {
                fun now(): Day {
                    val dateTime = LocalDateTime.now()
                    return Day(dateTime.year, dateTime.monthValue, dateTime.dayOfMonth)
                }
            }
        }

        class Custom(start: LocalDate, end: LocalDate) : DateRange(start, end)

        override fun equals(other: Any?): Boolean {
            return other is DateRange && other.start == start && other.end == end
        }

        override fun hashCode(): Int {
            var result = start.hashCode()
            result = 31 * result + end.hashCode()
            return result
        }
    }

    private val hhmFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    private suspend fun refreshRecords() = withContext(Dispatchers.IO) {
        val range = dateRange.value
        val start = range.start.zonedEpoch()
        val end = range.end.zonedEpoch()

        var expenditure = 0
        var income = 0

        val result = recordDao.loadAllByDateDesc(start, end).map {
            it.mapToRecord().also {
                if (it.money < 0) {
                    expenditure += abs(it.money)
                } else {
                    income += it.money
                }
            }
        }
        statistics.emit(Statistics(expenditure, income, 0)) // TODO: Budget
        val dailyRecords = groupToDailyRecord(result)
        records.emit(dailyRecords)
    }

    private suspend fun EntityRecord.mapToRecord(): Record {
        val instant = Instant.ofEpochSecond(this.timestamp)
        val zoned = instant.atZone(localZoneId)
        val time = hhmFormatter.format(zoned)
        val date = Date(dateFormatter.format(zoned), calculateDayOffset(zoned))
        val label = recordTypeDao.loadAllByIds(this.typeId).first().mapToRecordType()

        return Record(
            money = this.money,
            date = date,
            time = time,
            type = label,
            id = this.id
        )
    }

    private fun calculateDayOffset(date: ZonedDateTime): Int {
        val today = with(ZonedDateTime.now()) {
            ZonedDateTime.of(year, monthValue, dayOfMonth, 23, 59, 59, 999_999_999, zone)
        }
        val offsetDay = Duration.between(date, today).toDays().toInt()
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

        expenditureLabels.emit(expMap)
        incomeLabels.emit(incomeMap)
    }

    fun setDateRange(dateRange: DateRange) {
        this.dateRange.value = dateRange
        viewModelScope.launch(Dispatchers.Default) {
            refreshRecords()
        }
    }

    fun addPrimaryLabel(name: String, isIncomeLabel: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            // Check existence
            if (recordTypeDao.getRootByLabel(name) == null) {
                recordTypeDao.insertAll(EntityRecordType(0, name, null, isIncomeLabel))
                refreshLabels()
            } else {
                // TODO: Show error message to user
                error("Duplicated label name")
            }
        }
    }

    fun addSecondaryLabel(primaryLabel: String, name: String, isIncomeLabel: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
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
        viewModelScope.launch(Dispatchers.Default) {
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
                    Instant.now().epochSecond,
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
                if (it.money < 0) {
                    expenditure += abs(it.money)
                } else {
                    income += it.money
                }
            }
            DailyRecord(
                date = records.first().date,
                expenditure = expenditure,
                income = income,
                records = records
            )
        }
    }

    private suspend fun EntityRecordType.mapToRecordType(): RecordType {
        val parent = parentId?.let { recordTypeDao.loadAllByIds(it).firstOrNull() }
        return RecordType(label, parent?.label, isIncome)
    }

    fun deleteRecord(id: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            recordDao.delete(me.konyaco.keeptally.storage.entity.Record(id = id, 0, 0, 0, null))
            refreshRecords()
        }
    }
}