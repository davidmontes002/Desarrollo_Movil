package com.example.mini_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mini_3.ui.RedSocialApp
import com.example.mini_3.ui.theme.Mini_3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { Mini_3Theme { RedSocialApp() } }
    }
}
