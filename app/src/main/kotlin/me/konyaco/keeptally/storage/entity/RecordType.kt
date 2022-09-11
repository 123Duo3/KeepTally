package me.konyaco.keeptally.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecordType(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val label: String,
    val parentId: Int?,
    val isIncome: Boolean
)