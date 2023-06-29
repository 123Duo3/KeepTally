package com.konyaco.keeptally.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konyaco.keeptally.api.KeepTallyApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtherViewModel @Inject constructor(
    private val api: KeepTallyApi
) : ViewModel() {
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

    fun submitLogin() = viewModelScope.launch(Dispatchers.IO) {
        isLoggingIn = true
        try {
            val response = api.login(loginEmail, loginPassword)
            if (response.code == 0) {
                val profile = api.profile().data!!
                username = profile.username
                email = profile.email
                isLoggedIn = true
            } else {
                loginError = response.msg
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
}