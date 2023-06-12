package me.konyaco.keeptally.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

//@Entity
data class Budget(
    @PrimaryKey
    val id: Int,
    val budget: Int,
)
