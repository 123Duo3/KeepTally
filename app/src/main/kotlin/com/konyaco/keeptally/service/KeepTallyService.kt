package com.konyaco.keeptally.service

import com.konyaco.keeptally.api.KeepTallyApi
import com.konyaco.keeptally.api.model.PullRequest
import com.konyaco.keeptally.api.model.PushRequest
import com.konyaco.keeptally.storage.MyDataStore
import com.konyaco.keeptally.storage.SnowFlakeIDGenerator
import com.konyaco.keeptally.storage.database.AppDatabase
import com.konyaco.keeptally.storage.entity.Command
import com.konyaco.keeptally.storage.entity.Record
import com.konyaco.keeptally.storage.entity.RecordType
import com.konyaco.keeptally.viewmodel.zonedEpoch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

private const val TAG = "KeepTallyService"
@Singleton
class KeepTallyService @Inject constructor(
    private val dataStore: MyDataStore,
    private val database: AppDatabase,
    private val keepTallyApi: KeepTallyApi,
    private val commandExecutor: CommandExecutor
) {
    private val snowFlakeIDGenerator = SnowFlakeIDGenerator
    private val secretGenerator = SecretGenerator
    private val recordDao = database.recordDao()
    private val recordTypeDao = database.recordTypeDao()
    private val commandDao = database.commandDao()
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    suspend fun getRecordsByDateRange(start: LocalDate, end: LocalDate): List<Record> = withContext(Dispatchers.IO) {
        recordDao.loadAllByDateDesc(
            start.zonedEpoch(),
            end.zonedEpoch()
        )
    }

    suspend fun getRecordsByDateRangeAndTypes(start: LocalDate, end: LocalDate, types: List<Long>): List<Record> = withContext(Dispatchers.IO) {
        recordDao.loadAllByDateAndTypeDesc(
            start.zonedEpoch(),
            end.zonedEpoch(),
            types
        )
    }

    suspend fun getTypesByIds(typeId: Long): List<RecordType> = withContext(Dispatchers.IO) {
        recordTypeDao.getAllByIds(typeId)
    }

    suspend fun getPrimaryTypes(): List<RecordType> = withContext(Dispatchers.IO) {
        recordTypeDao.getAllRoot()
    }

    suspend fun getSecondaryTypesByPrimaryId(primaryId: Long): List<RecordType> = withContext(Dispatchers.IO) {
        recordTypeDao.getSubTypes(primaryId)
    }

    suspend fun addPrimaryLabel(label: String, isIncomeType: Boolean ) {
        if (recordTypeDao.getRootByLabel(label) == null) {
            recordTypeDao.insertAll(
                RecordType(
                    SnowFlakeIDGenerator.nextId(), label, null, isIncomeType
                )
            )
        } else {
            error("Duplicated label name")
        }
    }

    suspend fun addSecondaryType(primaryLabel: String, label: String, isIncomeType: Boolean) {
        val primary = recordTypeDao.getRootByLabel(primaryLabel)

        // Check existence
        if (primary != null && primary.isIncome == isIncomeType) {
            val exist =
                recordTypeDao.getSubTypes(primary.id).firstOrNull { it.label == label } != null

            if (!exist) {
                recordTypeDao.insertAll(RecordType(0, label, primary.id, isIncomeType))
            } else {
                error("The label was existed")
            }
        } else {
            error("Primary label was not found")
        }
    }
    suspend fun addRecord(
        isIncome: Boolean,
        money: Long,
        primaryLabel: String,
        secondaryLabel: String?,
        description: String?,
        date: LocalDateTime
    ) = withContext(Dispatchers.IO) {
        val primary = recordTypeDao.getRootByLabel(primaryLabel)
            ?: error("Primary label: $primaryLabel was not found")
        val label = if (secondaryLabel != null) {
            recordTypeDao.getSubTypes(primary.id)
                .firstOrNull { it.label == secondaryLabel }
                ?: error("Secondary label $secondaryLabel in $primaryLabel was not found")
        } else primary

        val money = if (isIncome) abs(money) else -abs(money)
        recordDao.insertAll(
            Record(
                SnowFlakeIDGenerator.nextId(),
                money,
                date.atZone(ZoneId.systemDefault()).toEpochSecond(),
                label.id,
                description
            )
        )
    }

    suspend fun deleteRecord(id: Long) = withContext(Dispatchers.IO) {
        val record = Record(id = id, 0, 0, 0, null)
        recordDao.delete(record)
        val now = Instant.now()
        val command = Command(
            id = snowFlakeIDGenerator.nextId(),
            command = json.encodeToString(CommandGenerator.deleteRecord(record)),
            operationTime = now.toEpochMilli(),
            commitTime = -1L
        )
        commandDao.insertAll(command)
    }

    suspend fun sync() = withContext(Dispatchers.IO) {
        coroutineScope {
            pull()
            push()
        }
    }

    private suspend fun pull() {
        val lastPullTime = dataStore.lastPullTime.first()
        val commands = keepTallyApi.pull(PullRequest(lastPullTime.toEpochMilli()))
        dataStore.setLastPullTime(Instant.now())
        commands.data!!.commands.forEach {
            // TODO: Decrypt data
            commandExecutor.execute(it.data)
        }
    }

    private suspend fun push() {
        val lastPushTime = dataStore.lastPushTime.first()
        val commands = commandDao.selectUncommitted()

        val pushRequest = PushRequest(
            commands = commands.map {
                // TODO: Encrypt data
                PushRequest.Command(
                    data = it.command,
                    snowFlakeId = it.id,
                    operationTime = it.operationTime
                )
            }
        )
        val now = Instant.now().toEpochMilli()
        commandDao.updateAll(*commands.map { it.copy(commitTime = now) }.toTypedArray())
        keepTallyApi.push(pushRequest)
        dataStore.setLastPushTime(Instant.now())
    }

    suspend fun getRecordsByLabels(labels: List<String>): List<RecordType> = withContext(Dispatchers.IO) {
        recordTypeDao.getAllByLabels(labels)
    }
}