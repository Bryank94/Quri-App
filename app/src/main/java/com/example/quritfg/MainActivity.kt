package com.example.quritfg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.quritfg.ui.navegacion.QuriApp
import com.example.quritfg.ui.theme.QuriTFGTheme

// punto de inicio cuando se abre la app.

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Permite que la interfaz ocupe toda la pantalla
        enableEdgeToEdge()

        // Define el contenido de la aplicación usando Jetpack Compose
        setContent {
            QuriTFGTheme {
                QuriApp()
            }
        }
    }
}