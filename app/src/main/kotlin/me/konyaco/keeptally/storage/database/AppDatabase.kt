package me.konyaco.keeptally.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
//import me.konyaco.keeptally.storage.dao.BudgetDao
import me.konyaco.keeptally.storage.dao.RecordDao
import me.konyaco.keeptally.storage.dao.RecordTypeDao
import me.konyaco.keeptally.storage.entity.Record
import me.konyaco.keeptally.storage.entity.RecordType

@Database(entities = [Record::class, RecordType::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun recordTypeDao(): RecordTypeDao
//    abstract fun budgetDao(): BudgetDao
}