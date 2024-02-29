package com.konyaco.keeptally.api.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val userId: Long
)