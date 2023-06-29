package com.konyaco.keeptally.api

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Cookie
import io.ktor.http.Url
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import com.konyaco.keeptally.BuildConfig
import com.konyaco.keeptally.storage.database.AppDatabase
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.time.Instant

class KeepTallyApi(
    private val database: AppDatabase
) {
    private val baseUrl = BuildConfig.BASE_URL + "/api"
    private val scope = CoroutineScope(Dispatchers.Default)

    private val httpClient = HttpClient {
        install(HttpCookies) {
            storage = DataStoreCookiesStorage(database)
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = false
            })
        }
    }

    @Serializable
    data class LoginRequest(
        val email: String,
        val password: String
    )

    @Serializable
    data class LoginResponse(
        val clientId: Long
    )

    suspend fun login(email: String, password: String): HttpResult<LoginResponse> {
        return httpClient.post("$baseUrl/user/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email, password))
        }.body()
    }

    @Serializable
    data class RegisterRequest(
        val username: String,
        val password: String,
        val email: String
    )

    @Serializable
    data class RegisterResponse(
        val userId: Long
    )

    suspend fun register(name: String, email: String, password: String): HttpResult<RegisterResponse> {
        return httpClient.post("$baseUrl/user/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest(name, password, email))
        }.body()
    }

    @Serializable
    data class ProfileResponse(
        val username: String,
        val email: String,
        val bio: String,
        val image: String
    )

    suspend fun profile(): HttpResult<ProfileResponse> {
        return httpClient.get("$baseUrl/user/profile").body()
    }

    @Serializable
    data class PushRequest(
        val commands: List<PushRequest>
    ) {
        data class Command(
            val type: String,
            val operationTime: Instant,
            val data: JsonObject
        )
    }

    suspend fun pull(): HttpResult<PullResponse> {
        return httpClient.get("$baseUrl/record/pull")
            .body()
    }

    data class PullResponse(
        val commands: List<PullResponse>
    ) {
        data class Command(
            val command: String,
            val operationTime: Instant,
            val commitTime: Instant
        )
    }

    suspend fun push(request: PushRequest): HttpResult<Unit> {
        return httpClient.post("$baseUrl/record/push") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}

private class DataStoreCookiesStorage(
    private val database: AppDatabase
) : CookiesStorage {
    override suspend fun get(requestUrl: Url): List<Cookie> {
        return database.cookieDao().getByRequestUrl(requestUrl.host).map {
            Cookie(it.name, it.value)
        }
    }

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        database.cookieDao().insertAll(
            com.konyaco.keeptally.storage.entity.Cookie(
                0,
                requestUrl.host,
                cookie.name,
                cookie.value
            )
        )
    }

    override fun close() {}
}
