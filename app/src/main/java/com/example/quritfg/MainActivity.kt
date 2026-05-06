package com.example.quritfg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.quritfg.ui.navegacion.QuriApp
import com.example.quritfg.ui.theme.QuriTFGTheme

/**
 * Actividad principal de la app.
 *
 * Es el punto de inicio, donde se carga toda la UI.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // permite usar toda la pantalla (sin margenes del sistema)
        enableEdgeToEdge()

        setContent {
            QuriTFGTheme {
                // aqui carga toda la app (navegacion incluida)
                QuriApp()
            }
        }
    }
}
