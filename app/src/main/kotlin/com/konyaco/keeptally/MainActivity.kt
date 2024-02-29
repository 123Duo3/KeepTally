package com.konyaco.keeptally

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.konyaco.keeptally.ui.App
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false);
        super.onCreate(savedInstanceState)

        setContent {
            App()
            /*val navController = rememberNavController()
            CompositionLocalProvider(LocalNavController provides navController) {
                NavHost(
                    navController = navController,
                    startDestination = "home",
                    builder = {
                        composable("home") {
                            App()
                        }
                    }
                )
            }*/
        }
    }
}