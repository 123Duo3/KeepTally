package com.konyaco.keeptally.api.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val username: String,
    val email: String,
    val bio: String,
    val image: String
)