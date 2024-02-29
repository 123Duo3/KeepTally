package com.konyaco.keeptally.storage.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.konyaco.keeptally.storage.entity.Budget


//@Dao
interface BudgetDao {
    @Query("SELECT * FROM budget")
    suspend fun getAll(): List<Budget>

    @Query("SELECT * FROM budget WHERE id = :id")
    suspend fun getById(id: Int): Budget?

    @Insert
    suspend fun insertAll(vararg budget: Budget)

    @Update
    suspend fun update(vararg budget: Budget)

    @Delete
    suspend fun delete(vararg budget: Budget)
}