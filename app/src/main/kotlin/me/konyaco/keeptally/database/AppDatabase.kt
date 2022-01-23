package me.konyaco.keeptally.database

import androidx.room.Database
import androidx.room.RoomDatabase
import me.konyaco.keeptally.dao.RecordDao
import me.konyaco.keeptally.dao.RecordTypeDao
import me.konyaco.keeptally.entity.Record
import me.konyaco.keeptally.entity.RecordType

@Database(entities = [Record::class, RecordType::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun recordTypeDao(): RecordTypeDao
}