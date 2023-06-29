package com.konyaco.keeptally.storage.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.konyaco.keeptally.storage.dao.CommandDao
import com.konyaco.keeptally.storage.dao.CookieDao
//import com.konyaco.keeptally.storage.dao.BudgetDao
import com.konyaco.keeptally.storage.dao.RecordDao
import com.konyaco.keeptally.storage.dao.RecordTypeDao
import com.konyaco.keeptally.storage.entity.Command
import com.konyaco.keeptally.storage.entity.Cookie
import com.konyaco.keeptally.storage.entity.Record
import com.konyaco.keeptally.storage.entity.RecordType

@Database(
    entities = [Record::class, RecordType::class, Cookie::class, Command::class], version = 2,
    autoMigrations = [AutoMigration(from = 1, to = 2)]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun recordTypeDao(): RecordTypeDao
    abstract fun cookieDao(): CookieDao
    abstract fun commandDao(): CommandDao
}

object Migration_1_2 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""alter table record modify id bigint not null;""")
        database.execSQL("""alter table record modify record_type bigint not null;""")
    }
}