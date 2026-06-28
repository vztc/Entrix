package com.apexbytestudio.entrix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.apexbytestudio.entrix.core.designsystem.theme.EntrixTheme
import com.apexbytestudio.entrix.ui.EntrixApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EntrixTheme {
                EntrixApp()
            }
        }
    }
}
