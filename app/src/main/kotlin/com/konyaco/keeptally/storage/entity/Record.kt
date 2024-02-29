package com.konyaco.keeptally.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.time.Instant

/**
 * @param money: If the record is income, money >= 0, else, money < 0
 */
@Entity
@Serializable
data class Record(
    @PrimaryKey val id: Long,
    val money: Long,
    val timestamp: Long = Instant.now().epochSecond,
    val typeId: Long,
    val description: String?
)