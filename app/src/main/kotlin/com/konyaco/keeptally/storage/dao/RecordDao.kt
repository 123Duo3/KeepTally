package com.konyaco.keeptally.storage.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.konyaco.keeptally.storage.entity.Record

@Dao
interface RecordDao {
    @Query("SELECT * FROM record")
    suspend fun getAll(): List<Record>

    @Query("SELECT * FROM record WHERE id IN (:ids)")
    suspend fun loadAllByIds(ids: IntArray): List<Record>

    @Query("SELECT * FROM record WHERE timestamp >= :start AND timestamp < :end ORDER BY timestamp")
    suspend fun loadAllByDate(start: Long, end: Long): List<Record>

    @Query("SELECT * FROM record WHERE timestamp >= :start AND timestamp < :end ORDER BY timestamp DESC")
    suspend fun loadAllByDateDesc(start: Long, end: Long): List<Record>

    @Query("SELECT * FROM record WHERE timestamp >= :start AND timestamp < :end AND typeId IN (:types) ORDER BY timestamp DESC")
    suspend fun loadAllByDateAndTypeDesc(start: Long, end: Long, types: List<Long>): List<Record>
    @Query("""SELECT * FROM record WHERE description LIKE :description LIMIT 1""")
    suspend fun findByDescription(description: String): Record

    @Query("""SELECT * FROM record WHERE typeId IN (:types)""")
    suspend fun loadAllByTypes(types: IntArray): List<Record>

    @Insert
    suspend fun insertAll(vararg records: Record)

    @Delete
    suspend fun delete(record: Record)

    @Query("DELETE FROM record WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Update
    suspend fun update(record: Record)
}