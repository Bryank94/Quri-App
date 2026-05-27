package com.example.quritfg.ui.pantallas.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quritfg.datos.SesionManager
import com.example.quritfg.datos.analytics.LocalAnalyticsTracker
import com.example.quritfg.datos.analytics.QuriAnalyticsEvents
import com.example.quritfg.datos.di.ModuloApp
import com.example.quritfg.ui.componentes.TarjetaQuri
import com.example.quritfg.ui.config.quriTexto
import com.example.quritfg.ui.navegacion.Rutas
import com.example.quritfg.ui.theme.DoradoDinero
import kotlinx.coroutines.launch

private data class PasoOnboarding(
    val titulo: String,
    val texto: String,
    val detalle: String
)

@Composable
fun OnboardingPantalla(navController: NavController) {
    val context = LocalContext.current
    val repositorio = ModuloApp.proporcionarRepositorio(context)
    val sesionManager = remember { SesionManager(context) }
    val analytics = remember { LocalAnalyticsTracker(context) }
    val scope = rememberCoroutineScope()
    var paso by remember { mutableIntStateOf(0) }
    val pasos = listOf(
        PasoOnboarding(
            titulo = quriTexto("Quri organiza tu sueldo", "Quri organizes your salary"),
            texto = quriTexto("Convierte cada ingreso en un plan claro para tus objetivos.", "Turn each income into a clear plan for your goals."),
            detalle = quriTexto("La idea es simple: cobras, Quri reparte y tu ves cuanto puedes gastar sin romper tus metas.", "The idea is simple: you get paid, Quri splits it and you see what you can spend without hurting your goals.")
        ),
        PasoOnboarding(
            titulo = quriTexto("Fondos virtuales", "Virtual funds"),
            texto = quriTexto("Viaje, sofa, emergencia... cada fondo muestra cuanto llevas y cuanto falta.", "Travel, sofa, emergency... each fund shows how much you have and what remains."),
            detalle = quriTexto("No movemos dinero real: primero validamos que el reparto se entienda y genere confianza.", "We do not move real money: first we validate that the split is understandable and trustworthy.")
        ),
        PasoOnboarding(
            titulo = quriTexto("Reparto automatico", "Automatic split"),
            texto = quriTexto("Quri sugiere cantidades y tu puedes cambiarlas antes de confirmar.", "Quri suggests amounts and you can change them before confirming."),
            detalle = quriTexto("Despues veras tu plan mensual, historial y reglas en Finanzas.", "Then you will see your monthly plan, history and rules in Finance.")
        )
    )
    val actual = pasos[paso]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TarjetaQuri {
            Text(
                text = actual.titulo,
                color = DoradoDinero,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = actual.texto,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = actual.detalle,
                color = Color.White.copy(alpha = 0.78f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${paso + 1} / ${pasos.size}",
                color = DoradoDinero,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (paso < pasos.lastIndex) {
            Button(
                onClick = { paso++ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(quriTexto("Siguiente", "Next"))
            }
        } else {
            Button(
                onClick = {
                    sesionManager.marcarOnboardingVisto()
                    analytics.track(QuriAnalyticsEvents.ONBOARDING_COMPLETED, mapOf("next" to "create_goal"))
                    navController.navigate(Rutas.ConfiguracionMeta.ruta) {
                        popUpTo(Rutas.Onboarding.ruta) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(quriTexto("Crear primer fondo", "Create first fund"))
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = {
                    scope.launch {
                        repositorio.cargarDatosDemoPreBeta()
                        sesionManager.marcarOnboardingVisto()
                        analytics.track(QuriAnalyticsEvents.ONBOARDING_COMPLETED, mapOf("next" to "demo"))
                        analytics.track(QuriAnalyticsEvents.DEMO_DATA_LOADED)
                        navController.navigate(Rutas.Finanzas.ruta) {
                            popUpTo(Rutas.Onboarding.ruta) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(quriTexto("Probar con datos de ejemplo", "Try sample data"))
            }
        }

        if (paso > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButton(onClick = { paso-- }) {
                    Text(quriTexto("Volver", "Back"))
                }
            }
        }
    }
}
