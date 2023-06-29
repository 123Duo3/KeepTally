package com.konyaco.keeptally.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecordType(
    @PrimaryKey val id: Int,
    val label: String,
    val parentId: Int?,
    val isIncome: Boolean
)