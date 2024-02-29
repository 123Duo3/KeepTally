package com.konyaco.keeptally.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class RecordType(
    @PrimaryKey val id: Long,
    val label: String,
    val parentId: Long?,
    val isIncome: Boolean
)