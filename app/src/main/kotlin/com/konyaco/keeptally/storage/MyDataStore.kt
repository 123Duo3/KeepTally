package com.konyaco.keeptally.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    data class UserStatus(
        val name: String,
        val email: String,
        val secret: String,
        val clientId: Long
    )

    val userStatus: Flow<UserStatus?>
        get() = dataStore.data.map {
            val name = it[PreferencesKeys.USER_STATUS_NAME]
            val email = it[PreferencesKeys.USER_STATUS_EMAIL]
            val secret = it[PreferencesKeys.USER_STATUS_SECRET]
            val clientId = it[PreferencesKeys.USER_STATUS_CLIENT_ID]
            if (name == null || email == null || secret == null || clientId == null) return@map null
            UserStatus(
                name = name,
                email = email,
                secret = secret,
                clientId = clientId
            )
        }

    suspend fun saveUserStatus(userStatus: UserStatus) {
        dataStore.edit {
            it[PreferencesKeys.USER_STATUS_NAME] = userStatus.name
            it[PreferencesKeys.USER_STATUS_EMAIL] = userStatus.email
            it[PreferencesKeys.USER_STATUS_SECRET] = userStatus.secret
            it[PreferencesKeys.USER_STATUS_CLIENT_ID] = userStatus.clientId
        }
    }

    suspend fun clearUserStatus() {
        dataStore.edit {
            it.remove(PreferencesKeys.USER_STATUS_NAME)
            it.remove(PreferencesKeys.USER_STATUS_EMAIL)
            it.remove(PreferencesKeys.USER_STATUS_SECRET)
            it.remove(PreferencesKeys.USER_STATUS_CLIENT_ID)
        }
    }

    val lastPushTime: Flow<Instant>
        get() = dataStore.data.map {
            it[PreferencesKeys.LAST_PUSH_TIME]?.let {
                Instant.parse(it)
            } ?: Instant.EPOCH
        }

    suspend fun setLastPushTime(instant: Instant) {
        dataStore.edit {
            it[PreferencesKeys.LAST_PUSH_TIME] = instant.toString()
        }
    }

    val lastPullTime: Flow<Instant>
        get() = dataStore.data.map {
            it[PreferencesKeys.LAST_PULL_TIME]?.let {
                Instant.parse(it)
            } ?: Instant.EPOCH
        }

    suspend fun setLastPullTime(instant: Instant) {
        dataStore.edit {
            it[PreferencesKeys.LAST_PULL_TIME] = instant.toString()
        }
    }
}

object PreferencesKeys {
    val USER_STATUS_NAME = stringPreferencesKey("user_status_name")
    val USER_STATUS_EMAIL = stringPreferencesKey("user_status_email")
    val USER_STATUS_SECRET = stringPreferencesKey("user_status_secret")
    val USER_STATUS_CLIENT_ID = longPreferencesKey("user_status_client_id")
    val LAST_PUSH_TIME = stringPreferencesKey("last_push_time")
    val LAST_PULL_TIME = stringPreferencesKey("last_pull_time")
}