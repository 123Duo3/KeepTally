package com.konyaco.keeptally.api.model

import kotlinx.serialization.Serializable

@Serializable
data class HttpResult<T>(
    val code: Int,
    val msg: String,
    val data: T?
) {
    companion object {
        fun <T> success(data: T?): HttpResult<T> {
            return HttpResult(0, "success", data)
        }
        
        fun <T> error(code: Int, msg: String): HttpResult<T> {
            return HttpResult(code, msg, null)
        }
    }
}