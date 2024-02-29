package com.konyaco.keeptally.storage.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.konyaco.keeptally.storage.entity.Command

@Dao
interface CommandDao {
    @Insert
    fun insertAll(vararg command: Command)
    @Delete
    fun delete(command: Command)
    @Query("SELECT * FROM command WHERE operationTime > :time")
    fun selectAfter(time: Long): List<Command>
    @Query("SELECT * FROM command WHERE commitTime = -1")
    fun selectUncommitted(): List<Command>
    @Update
    fun updateAll(vararg command: Command)
}