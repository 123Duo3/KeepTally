package com.konyaco.keeptally.api

import kotlinx.serialization.Serializable

@Serializable
data class HttpResult<T>(
    val code: Int,
    val msg: String,
    val data: T?
)