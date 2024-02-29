package com.konyaco.keeptally.service

import com.konyaco.keeptally.storage.database.AppDatabase
import com.konyaco.keeptally.storage.entity.Record
import com.konyaco.keeptally.storage.entity.RecordType
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommandExecutor @Inject constructor(
    private val database: AppDatabase
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun execute(command: String) {
        val commandData = json.decodeFromString(CommandGenerator.CommandData.serializer(), command)
        when (commandData.type) {
            "record_create" -> {
                val record = json.decodeFromJsonElement(Record.serializer(), commandData.data)
                database.recordDao().insertAll(record)
            }

            "record_update" -> {
                val record = json.decodeFromJsonElement(Record.serializer(), commandData.data)
                database.recordDao().update(record)
            }

            "record_delete" -> {
                val id = json.decodeFromJsonElement(
                    MapSerializer(
                        String.serializer(),
                        Long.serializer()
                    ), commandData.data
                )["id"]!!
                database.recordDao().deleteById(id)
            }

            "record_type_create" -> {
                val recordType =
                    json.decodeFromJsonElement(RecordType.serializer(), commandData.data)
                database.recordTypeDao().insertAll(recordType)
            }

            "record_type_update" -> {
                val recordType =
                    json.decodeFromJsonElement(RecordType.serializer(), commandData.data)
                database.recordTypeDao().update(recordType)
            }

            "record_type_delete" -> {
                val id = json.decodeFromJsonElement(
                    MapSerializer(
                        String.serializer(),
                        Long.serializer()
                    ), commandData.data
                )["id"]!!
                database.recordTypeDao().deleteById(id)
            }
        }
    }
}