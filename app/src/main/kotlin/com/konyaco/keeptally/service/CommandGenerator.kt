package com.konyaco.keeptally.service

import com.konyaco.keeptally.storage.entity.Record
import com.konyaco.keeptally.storage.entity.RecordType
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement

/**
 * This class is responsible for generating commands json to be sent to the server.
 */
object CommandGenerator {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    @Serializable
    data class CommandData(
        val type: String,
        val data: JsonElement
    )

    fun createRecord(record: Record): JsonElement {
        val data = json.encodeToJsonElement(record)
        return json.encodeToJsonElement(CommandData("record_create", data))
    }

    fun updateRecord(record: Record): JsonElement {
        val data = json.encodeToJsonElement(record)
        return json.encodeToJsonElement(CommandData("record_update", data))
    }

    fun deleteRecord(record: Record): JsonElement {
        val data = json.encodeToJsonElement(mapOf("id" to record.id,))
        return json.encodeToJsonElement(CommandData("record_delete", data))
    }

    fun createRecordType(recordType: RecordType): JsonElement {
        val data = json.encodeToJsonElement(recordType)
        return json.encodeToJsonElement(CommandData("record_type_create", data))
    }

    fun updateRecordType(recordType: RecordType): JsonElement {
        val data = json.encodeToJsonElement(recordType)
        return json.encodeToJsonElement(CommandData("record_type_update", data))
    }

    fun deleteRecordType(recordType: RecordType): JsonElement {
        val data = json.encodeToJsonElement(mapOf("id" to recordType.id))
        return json.encodeToJsonElement(CommandData("record_type_delete", data))
    }
}