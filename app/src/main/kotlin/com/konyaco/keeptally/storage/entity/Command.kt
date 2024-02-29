package com.konyaco.keeptally.storage.entity

import androidx.room.Entity
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey

@Entity
data class Command(
    @PrimaryKey
    val id: Long,
    val command: String,
    val operationTime: Long,
    val commitTime: Long
)