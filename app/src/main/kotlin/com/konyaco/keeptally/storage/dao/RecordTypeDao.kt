package com.konyaco.keeptally.storage.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.konyaco.keeptally.storage.entity.RecordType

@Dao
interface RecordTypeDao {
    @Query("SELECT count(*) FROM recordtype")
    suspend fun count(): Long

    @Query("SELECT * FROM recordtype")
    suspend fun getAll(): List<RecordType>

    @Query("SELECT * FROM recordtype WHERE isIncome = 1")
    suspend fun getAllIncome(): List<RecordType>

    @Query("SELECT * FROM recordtype WHERE isIncome = 0")
    suspend fun getAllExpenditure(): List<RecordType>

    @Query("SELECT * FROM recordtype WHERE id IN (:id)")
    suspend fun getAllByIds(vararg id: Long): List<RecordType>

    @Query("SELECT * FROM recordtype WHERE parentId IS NULL")
    suspend fun getAllRoot(): List<RecordType>

    @Query("SELECT * FROM recordtype WHERE label LIKE :label")
    suspend fun getByLabel(label: String): List<RecordType>

    @Query("SELECT * FROM recordtype WHERE label IN (:labels)")
    suspend fun getAllByLabels(labels: List<String>): List<RecordType>

    @Query("SELECT * FROM recordtype WHERE label LIKE :label AND parentId IS NULL")
    suspend fun getRootByLabel(label: String): RecordType?

    @Query("SELECT * FROM recordtype WHERE parentId = :parentId")
    suspend fun getSubTypes(parentId: Long): List<RecordType>

    @Insert
    suspend fun insertAll(vararg types: RecordType): List<Long>

    @Delete
    suspend fun delete(type: RecordType)

    @Query("DELETE FROM recordtype WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Update
    suspend fun update(recordType: RecordType)
}