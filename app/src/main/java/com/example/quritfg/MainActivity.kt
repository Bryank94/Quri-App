package com.example.quritfg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import com.example.quritfg.ui.componentes.BarraSuperior
import com.example.quritfg.ui.navegacion.QuriApp
import com.example.quritfg.ui.theme.QuriTFGTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            QuriTFGTheme {
                Column {
                    BarraSuperior() // 🔥 añadimos la barra aquí

                    QuriApp() // 👈 tu navegación sigue funcionando igual
                }
            }
        }
    }
}