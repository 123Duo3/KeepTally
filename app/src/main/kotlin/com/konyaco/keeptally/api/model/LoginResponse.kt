package com.konyaco.keeptally.api.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val clientId: Long
)
