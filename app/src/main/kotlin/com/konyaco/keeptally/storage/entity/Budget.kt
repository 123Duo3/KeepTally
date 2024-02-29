package com.konyaco.keeptally.storage.entity

import androidx.room.PrimaryKey

//@Entity
data class Budget(
    @PrimaryKey
    val id: Int,
    val budget: Int,
)
