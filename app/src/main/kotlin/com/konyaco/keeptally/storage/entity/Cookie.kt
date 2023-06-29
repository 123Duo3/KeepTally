package com.konyaco.keeptally.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cookie(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val url: String,
    val name: String,
    val value: String
)
