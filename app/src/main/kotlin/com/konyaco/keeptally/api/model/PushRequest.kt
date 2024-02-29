package com.konyaco.keeptally.api.model

import kotlinx.serialization.Serializable

@Serializable
data class PushRequest(
    val commands: List<Command>
) {
    @Serializable
    data class Command(
        val data: String,
        val snowFlakeId: Long,
        val operationTime: Long
    )
}