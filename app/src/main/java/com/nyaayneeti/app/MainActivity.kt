package com.nyaayneeti.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.nyaayneeti.app.ui.navigation.NyaayNavGraph
import com.nyaayneeti.app.ui.theme.NyaayTheme
import com.nyaayneeti.app.ui.theme.resolveDarkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val container = (application as NyaayApplication).container
        setContent {
            val themeMode by container.authRepository.themeMode.collectAsState()
            NyaayTheme(darkTheme = resolveDarkTheme(themeMode, isSystemInDarkTheme())) {
                NyaayNavGraph(container)
            }
        }
    }
}
