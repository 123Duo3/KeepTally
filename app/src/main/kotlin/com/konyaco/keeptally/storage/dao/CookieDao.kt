package com.konyaco.keeptally.storage.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.konyaco.keeptally.storage.entity.Cookie

@Dao
interface CookieDao {
    @Query("SELECT * FROM cookie WHERE url = :url")
    fun getByRequestUrl(url: String): List<Cookie>

    @Query("SELECT * FROM cookie WHERE url = :url and name = :name")
    fun getByRequestUrlAndName(url: String, name: String): List<Cookie>

    @Insert
    fun insertAll(vararg cookie: Cookie)

    @Delete
    fun delete(cookie: Cookie)

    @Query("DELETE FROM cookie")
    fun deleteAll()

    @Query("DELETE FROM cookie WHERE url = :url")
    fun deleteByUrl(url: String)
}