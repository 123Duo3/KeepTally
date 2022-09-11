package me.konyaco.keeptally.ui.other

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.konyaco.keeptally.ui.other.component.OptionList
import me.konyaco.keeptally.ui.other.component.UserProfile
import me.konyaco.keeptally.ui.theme.KeepTallyTheme

@Composable
fun OtherScreen() {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        UserProfile(
            modifier = Modifier.fillMaxWidth(),
            userName = "示例用户",
            email = "user@example.com",
            alipayBalance = 12345,
            wechatBalance = 12455
        )
        Spacer(Modifier.height(16.dp))
        OptionList(Modifier.fillMaxWidth())
    }
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