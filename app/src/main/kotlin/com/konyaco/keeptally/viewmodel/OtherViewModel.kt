package com.konyaco.keeptally.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konyaco.keeptally.api.KeepTallyApi
import com.konyaco.keeptally.service.SecretGenerator
import com.konyaco.keeptally.storage.MyDataStore
import com.konyaco.keeptally.storage.SnowFlakeIDGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class OtherViewModel @Inject constructor(
    private val api: KeepTallyApi,
    private val sharedViewModel: SharedViewModel,
    private val myDataStore: MyDataStore
) : ViewModel() {
    var initialized by mutableStateOf(false)
    var isLoggedIn by mutableStateOf(false)
    var isLoggingIn by mutableStateOf(false)

    var username by mutableStateOf("未登录")
    var email by mutableStateOf("未登录")
    var bio by mutableStateOf("")
    var image by mutableStateOf("")

    var alipayBalance by mutableStateOf("123.4")
    var wechatBalance by mutableStateOf("123.4")

    var showLoginDialog by mutableStateOf(false)

    var loginEmail by mutableStateOf("")
    var loginPassword by mutableStateOf("")
    var loginError by mutableStateOf<String?>(null)


    var isRegistering by mutableStateOf(false)
    var showRegisterDialog by mutableStateOf(false)
    var registerEmail by mutableStateOf("")
    var registerPassword by mutableStateOf("")
    var registerPasswordConfirm by mutableStateOf("")
    var registerName by mutableStateOf("")
    var registerResult by mutableStateOf<String?>(null)

    var isSyncing  by sharedViewModel.isSyncing

    var showLogoutDialog by mutableStateOf(false)

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            suspendCoroutine<Unit> {cont ->
                launch {
                    myDataStore.userStatus.take(1).collectLatest {
                        if (it != null) {
                            isLoggedIn = true
                            username = it.name
                            email = it.email
                            SnowFlakeIDGenerator.setWorkerId(it.clientId)
                        }
                        cont.resume(Unit)
                    }
                    api.profile().data?.let {
                        username = it.username
                        email = it.email
                        bio = it.bio
                        image = it.image
                    }
                }
            }
            initialized = true
        }
    }

    fun submitLogin() = viewModelScope.launch(Dispatchers.IO) {
        isLoggingIn = true
        try {
            val resp = api.login(loginEmail, loginPassword)
            if (resp.code == 0) {
                val clientId = resp.data!!.clientId
                val profile = api.profile().data!!

                myDataStore.saveUserStatus(
                    MyDataStore.UserStatus(
                        name = profile.username,
                        email = profile.email,
                        secret = SecretGenerator.generateSecret(loginPassword),
                        clientId = clientId,
                    )
                )
                SnowFlakeIDGenerator.setWorkerId(clientId)
                // Login succeed
                username = profile.username
                email = profile.email
                showLoginDialog = false
                isLoggedIn = true
            } else {
                loginError = resp.msg
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            loginError = "出错，请稍后再试"
        } finally {
            isLoggingIn = false
        }
    }

    fun handleLogin() {
        showLoginDialog = true
    }

    fun closeLoginDialog() {
        showLoginDialog = false
    }

    fun handleRegister() {
        showLoginDialog = false
        showRegisterDialog = true
    }

    fun submitRegister() = viewModelScope.launch(Dispatchers.IO) {
        if (registerPassword != registerPasswordConfirm) {
            registerResult = "密码不一致"
            return@launch
        }
        isRegistering = true
        try {
            val resp = api.register(registerName, registerEmail, registerPassword)
            if (resp.code == 0) {
                registerResult = "注册成功"
            } else {
                registerResult = resp.msg
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            registerResult = "出错，请稍后再试"
        } finally {
            isRegistering = false
        }
    }

    fun handleBack() {
        showRegisterDialog = false
        showLoginDialog = true
    }

    fun handleLogout() {
        showLogoutDialog = true
    }

    fun submitLogout() = viewModelScope.launch(Dispatchers.IO) {
        myDataStore.clearUserStatus()
        isLoggedIn = false
        username = "未登录"
        email = "未登录"
        showLogoutDialog = false
    }

    fun closeLogoutDialog() {
        showLogoutDialog = false
    }
}