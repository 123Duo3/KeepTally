package com.konyaco.keeptally.service

import kotlinx.serialization.json.JsonObject
import com.konyaco.keeptally.storage.entity.Record
import com.konyaco.keeptally.storage.entity.RecordType

/**
 * This class is responsible for generating commands json to be sent to the server.
 */
object CommandGenerator {
    fun createRecord(record: Record): JsonObject {
        TODO()
    }

    fun updateRecord(record: Record): JsonObject {
        TODO()
    }

    fun deleteRecord(record: Record): JsonObject {
        TODO()
    }

    fun createRecordType(recordType: RecordType): JsonObject {
        TODO()
    }

    fun updateRecordType(recordType: RecordType): JsonObject {
        TODO()
    }

    fun deleteRecordType(recordType: RecordType): JsonObject {
        TODO()
    }
}