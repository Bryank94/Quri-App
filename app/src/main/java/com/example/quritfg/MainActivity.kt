package com.example.quritfg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.example.quritfg.datos.analytics.LocalAnalyticsTracker
import com.example.quritfg.datos.analytics.QuriAnalyticsEvents
import com.example.quritfg.ui.config.LocalQuriSettings
import com.example.quritfg.ui.config.rememberQuriSettings
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
        LocalAnalyticsTracker(this).track(QuriAnalyticsEvents.APP_OPENED)

        setContent {
            val settings = rememberQuriSettings(LocalContext.current)

            CompositionLocalProvider(LocalQuriSettings provides settings) {
                QuriTFGTheme(darkTheme = settings.temaOscuro) {
                    // aqui carga toda la app (navegacion incluida)
                    QuriApp()
                }
            }
        }
    }
}

