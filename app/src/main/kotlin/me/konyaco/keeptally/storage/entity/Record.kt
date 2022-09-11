package me.konyaco.keeptally.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity
data class Record(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val money: Int,
    val timestamp: Long = Instant.now().epochSecond,
    val typeId: Int,
    val description: String?
)