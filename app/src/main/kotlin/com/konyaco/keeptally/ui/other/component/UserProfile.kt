package com.konyaco.keeptally.ui.other.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konyaco.keeptally.R
import com.konyaco.keeptally.viewmodel.model.RecordSign
import com.konyaco.keeptally.ui.theme.KeepTallyTheme
import com.konyaco.keeptally.ui.theme.RobotoSlab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfile(
    modifier: Modifier,
    isLogin: Boolean,
    userName: String,
    email: String,
    alipayBalance: String,
    wechatBalance: String,
    onLoginClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        onClick = onLoginClick
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    modifier = Modifier.size(32.dp),
                    contentDescription = "Avatar",
                    imageVector = Icons.Default.Person,
                    colorFilter = ColorFilter.tint(LocalContentColor.current)
                )
                Column(Modifier.weight(1f)) {
                    Text(text = userName, style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                    )
                }
                Icon(imageVector = Icons.Default.CloudDone, contentDescription = "Backup")
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Forward"
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth()) {
                AlipayButton(money = alipayBalance, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(16.dp))
                WechatPayButton(money = wechatBalance, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun PaymentButton(
    icon: Painter,
    text: String,
    money: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        modifier = modifier,
        onClick = { /*TODO*/ },
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 6.dp, horizontal = 16.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
    ) {
        Icon(
            icon,
            contentDescription = text,
            tint = color,
        )
        Spacer(
            Modifier
                .width(8.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
        Divider(
            Modifier
                .padding(horizontal = 4.dp)
                .height(12.dp)
                .width(1.dp)
        )
        Text(
            modifier = Modifier.weight(1f),
            text = "$money${RecordSign.RMB}",
            color = LocalContentColor.current.copy(0.8f),
            style = MaterialTheme.typography.labelLarge,
            fontFamily = FontFamily.RobotoSlab,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun AlipayButton(money: String, modifier: Modifier) {
    PaymentButton(
        icon = painterResource(id = R.drawable.ic_alipay),
        text = "支付宝",
        money = money,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

@Composable
private fun WechatPayButton(money: String, modifier: Modifier) {
    PaymentButton(
        icon = painterResource(id = R.drawable.ic_wechatpay),
        text = "微信",
        money = money,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}


@Preview
@Composable
private fun Preview() {
    KeepTallyTheme {
        UserProfile(
            modifier = Modifier.fillMaxWidth(),
            userName = "示例用户",
            email = "user@email.com",
            alipayBalance = "123.45",
            wechatBalance = "123.45",
            onLoginClick = {},
            isLogin = false
        )
    }
}