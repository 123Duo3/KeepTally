package com.konyaco.keeptally.ui.other

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.konyaco.keeptally.ui.other.component.OptionList
import com.konyaco.keeptally.ui.other.component.UserProfile
import com.konyaco.keeptally.ui.theme.KeepTallyTheme
import com.konyaco.keeptally.viewmodel.OtherViewModel

@Composable
fun OtherScreen(
    viewModel: OtherViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.init()
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        UserProfile(
            modifier = Modifier.fillMaxWidth(),
            userName = viewModel.username,
            email = viewModel.email,
            alipayBalance = viewModel.alipayBalance,
            wechatBalance = viewModel.wechatBalance,
            isLogin = viewModel.isLoggedIn,
            onLoginClick = {
                if (viewModel.isLoggedIn) viewModel.handleLogout()
                else viewModel.handleLogin()
            },
            isSyncing = viewModel.isSyncing
        )
        Spacer(Modifier.height(16.dp))
        OptionList(Modifier.fillMaxWidth())

        LoginDialog(viewModel)
        RegisterDialog(viewModel)
        LogoutDialog(viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginDialog(
    viewModel: OtherViewModel
) {
    if (viewModel.showLoginDialog) Dialog(onDismissRequest = {
        viewModel.closeLoginDialog()
    }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "登录",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                TextField(
                    value = viewModel.loginEmail, onValueChange = {
                        viewModel.loginEmail = it
                    },
                    label = { Text("邮箱") },
                    isError = viewModel.loginError != null,
                    supportingText = {
                        if (viewModel.loginError != null) {
                            Text(
                                text = viewModel.loginError!!,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                )
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = viewModel.loginPassword,
                    onValueChange = { viewModel.loginPassword = it },
                    label = { Text("密码") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = viewModel.loginError != null,
                    supportingText = {
                        if (viewModel.loginError != null) {
                            Text(
                                text = viewModel.loginError!!,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                )
                Spacer(Modifier.height(16.dp))
                Row {
                    Button(
                        onClick = { viewModel.submitLogin() },
                        enabled = !viewModel.isLoggingIn
                    ) {
                        if (viewModel.isLoggingIn) Text("登陆中")
                        else Text("登录")
                    }
                    Spacer(Modifier.width(12.dp))
                    Button(onClick = { viewModel.handleRegister() }) {
                        Text("注册")
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterDialog(viewModel: OtherViewModel) {
    if (viewModel.showRegisterDialog) Dialog(onDismissRequest = {
        viewModel.closeLoginDialog()
    }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "注册",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                TextField(
                    value = viewModel.registerName, onValueChange = {
                        viewModel.registerName = it
                    },
                    label = { Text("昵称") }
                )
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = viewModel.registerEmail, onValueChange = {
                        viewModel.registerEmail = it
                    },
                    label = { Text("邮箱") }
                )
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = viewModel.registerPassword,
                    onValueChange = { viewModel.registerPassword = it },
                    label = { Text("密码") },
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = viewModel.registerPasswordConfirm,
                    onValueChange = { viewModel.registerPasswordConfirm = it },
                    label = { Text("确认密码") },
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(Modifier.height(16.dp))
                viewModel.registerResult?.let {
                    Text(text = it)
                }
                Row {
                    Button(onClick = {
                        viewModel.handleBack()
                    }) {
                        Text("返回")
                    }
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = { viewModel.submitRegister() },
                        enabled = !viewModel.isLoggingIn
                    ) {
                        if (viewModel.isRegistering) Text("注册中")
                        else Text("注册")
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginButton(
    modifier: Modifier,
    icon: Painter,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        onClick = onClick
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LogoutDialog(viewModel: OtherViewModel) {
    if (viewModel.showLogoutDialog) AlertDialog(
        onDismissRequest = { viewModel.closeLogoutDialog() },
        confirmButton = {
            Button(onClick = { viewModel.submitLogout() }) {
                Text("注销")
            }
        },
        text = { Text("注销登录？") }
    )
}

@Preview
@Composable
private fun Preview() {
    KeepTallyTheme {
        Surface(color = MaterialTheme.colorScheme.inverseOnSurface) {
            OtherScreen()
        }
    }
}