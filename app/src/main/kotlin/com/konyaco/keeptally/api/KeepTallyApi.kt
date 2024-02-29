package com.konyaco.keeptally.api

import com.konyaco.keeptally.BuildConfig
import com.konyaco.keeptally.api.model.LoginRequest
import com.konyaco.keeptally.api.model.LoginResponse
import com.konyaco.keeptally.api.model.ProfileResponse
import com.konyaco.keeptally.api.model.PullRequest
import com.konyaco.keeptally.api.model.PullResponse
import com.konyaco.keeptally.api.model.PushRequest
import com.konyaco.keeptally.api.model.RegisterRequest
import com.konyaco.keeptally.api.model.RegisterResponse
import com.konyaco.keeptally.storage.database.AppDatabase
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Cookie
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json

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

    private var onAuthenticationListener: ((Boolean) -> Unit)? = null

    private suspend inline fun <reified T> post(path: String, body: Any): HttpResult<T> {
        val data = httpClient.post("$baseUrl$path") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body<HttpResult<T>>()
        if (data.code == 403) {
            onAuthenticationListener?.invoke(false)
            error("Not authenticated")
        }
        return data
    }

    private suspend inline fun <reified T> get(path: String): HttpResult<T> {
        val data = httpClient.get("$baseUrl$path").body<HttpResult<T>>()
        if (data.code == 403) {
            onAuthenticationListener?.invoke(false)
            error("Not authenticated")
        }
        return data
    }

    suspend fun login(request: LoginRequest): HttpResult<LoginResponse> =
        post("/user/login", request)

    suspend fun register(request: RegisterRequest): HttpResult<RegisterResponse> =
        post("/user/register", request)

    suspend fun profile(): HttpResult<ProfileResponse> =
        get("/user/profile")

    suspend fun pull(request: PullRequest): HttpResult<PullResponse> =
        post("/record/pull", request)

    suspend fun push(request: PushRequest): HttpResult<Unit> =
        post("/record/push", request)

    suspend fun logout(): HttpResult<Unit> =
        get("/user/logout")
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
        val cookies = database.cookieDao().getByRequestUrlAndName(requestUrl.host, cookie.name)
        cookies.forEach { database.cookieDao().delete(it) }
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
