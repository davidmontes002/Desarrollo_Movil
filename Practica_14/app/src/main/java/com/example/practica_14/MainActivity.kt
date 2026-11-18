package com.example.practica_14

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.practica_14.ui.theme.Practica_14Theme
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().apply {
            load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))
            userAgentValue = "Practica14_GPS_Room/1.0"
        }
        enableEdgeToEdge()
        setContent {
            Practica_14Theme { MapaOSMScreen() }
        }
    }
}
