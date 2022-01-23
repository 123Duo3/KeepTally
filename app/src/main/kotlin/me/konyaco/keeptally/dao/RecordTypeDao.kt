package me.konyaco.keeptally.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import me.konyaco.keeptally.entity.RecordType

@Dao
interface RecordTypeDao {
    @Query("SELECT * FROM recordtype")
    suspend fun getAll(): List<RecordType>

    @Query("SELECT * FROM recordtype WHERE id IN (:id)")
    suspend fun loadAllByIds(vararg id: Int): List<RecordType>

    @Query("SELECT * FROM recordtype WHERE parentId IS NULL")
    suspend fun getAllRoot(): List<RecordType>

    @Query("SELECT * FROM recordtype WHERE label LIKE :label")
    suspend fun getByLabel(label: String): List<RecordType>

    @Query("SELECT * FROM recordtype WHERE label LIKE :label AND parentId IS NULL")
    suspend fun getRootByLabel(label: String): RecordType?

    @Query("SELECT * FROM recordtype WHERE parentId = :parentId")
    suspend fun getSubTypes(parentId: Int): List<RecordType>

    @Insert
    suspend fun insertAll(vararg types: RecordType)

    @Delete
    suspend fun delete(type: RecordType)
}