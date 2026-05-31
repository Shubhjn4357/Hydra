package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import com.example.ui.WaterLoggerApp
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.WaterViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class MainActivity : ComponentActivity() {
    
    private val viewModel: WaterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeOverride by viewModel.themeOverride.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()
            val isDark = themeOverride ?: systemDark

            MyApplicationTheme(darkTheme = isDark) {
                WaterLoggerApp(
                    viewModel = viewModel,
                    isDarkTheme = isDark,
                    onDarkThemeToggle = { viewModel.setThemePreference(it) }
                )
            }
        }
    }
}
