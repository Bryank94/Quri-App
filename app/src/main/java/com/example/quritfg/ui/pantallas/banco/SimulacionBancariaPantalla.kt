package com.example.quritfg.ui.pantallas.banco

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quritfg.datos.analytics.LocalAnalyticsTracker
import com.example.quritfg.datos.analytics.QuriAnalyticsEvents
import com.example.quritfg.datos.bank.BankProvider
import com.example.quritfg.datos.bank.DemoBankDataSource
import com.example.quritfg.ui.componentes.EncabezadoTarjetaQuri
import com.example.quritfg.ui.componentes.FilaDatoQuri
import com.example.quritfg.ui.componentes.SeparadorQuri
import com.example.quritfg.ui.componentes.TarjetaQuri
import com.example.quritfg.ui.componentes.TituloPantallaQuri
import com.example.quritfg.ui.config.formatearDineroQuri
import com.example.quritfg.ui.config.quriTexto
import com.example.quritfg.ui.theme.DoradoDinero
import kotlinx.coroutines.launch

@Composable
fun SimulacionBancariaPantalla(navController: NavController) {
    val context = LocalContext.current
    val analytics = remember { LocalAnalyticsTracker(context) }
    val scope = rememberCoroutineScope()
    val demoBank = remember { DemoBankDataSource() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TituloPantallaQuri(quriTexto("Banco real", "Real bank"))

        TarjetaQuri {
            EncabezadoTarjetaQuri("BANK", quriTexto("Fase 1: solo lectura", "Phase 1: read only"))
            Text(
                quriTexto(
                    "El siguiente paso es conectar el banco para detectar nominas y movimientos reales. Quri solo leera datos con permiso del usuario.",
                    "The next step is connecting a bank to detect real salaries and movements. Quri will only read data with user permission."
                ),
                color = Color.White
            )
            SeparadorQuri()
            FilaDatoQuri("OK", quriTexto("Mover dinero", "Move money"), quriTexto("No", "No"), DoradoDinero)
            FilaDatoQuri("OK", quriTexto("Wallet o IBAN propio", "Wallet or own IBAN"), quriTexto("No", "No"), DoradoDinero)
            FilaDatoQuri("OK", quriTexto("Leer movimientos", "Read transactions"), quriTexto("Si, con consentimiento", "Yes, with consent"), DoradoDinero)
        }

        TarjetaQuri {
            EncabezadoTarjetaQuri("DOC", quriTexto("Proveedores candidatos", "Candidate providers"))
            Text(
                quriTexto(
                    "Estos proveedores se evaluaran para PSD2/Open Banking en Europa.",
                    "These providers will be evaluated for PSD2/Open Banking in Europe."
                ),
                color = Color.White.copy(alpha = 0.82f)
            )
            BankProvider.entries.forEach { provider ->
                ProviderRow(provider = provider) {
                    analytics.track(
                        QuriAnalyticsEvents.BANK_PROVIDER_SELECTED,
                        mapOf("provider" to provider.displayName)
                    )
                }
                SeparadorQuri()
            }
        }

        TarjetaQuri {
            EncabezadoTarjetaQuri("Q", quriTexto("Medir interes", "Measure interest"))
            Text(
                quriTexto(
                    "Este boton no conecta aun el banco. Sirve para medir cuantos testers quieren esta funcion.",
                    "This button does not connect a bank yet. It measures how many testers want this feature."
                ),
                color = Color.White
            )
            Button(
                onClick = {
                    analytics.track(QuriAnalyticsEvents.BANK_CONNECT_INTEREST)
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DoradoDinero, contentColor = Color(0xFF06160D))
            ) {
                Text(quriTexto("Me interesa conectar mi banco", "I want to connect my bank"), fontWeight = FontWeight.Bold)
            }
        }

        TarjetaQuri {
            EncabezadoTarjetaQuri("HIST", quriTexto("Sincronizacion demo", "Demo sync"))
            Text(
                quriTexto(
                    "Esto simula lo que Quri recibira del banco: nomina, gastos y categorias.",
                    "This simulates what Quri will receive from the bank: salary, expenses and categories."
                ),
                color = Color.White.copy(alpha = 0.82f)
            )
            OutlinedButton(
                onClick = {
                    scope.launch {
                        val transacciones = demoBank.getTransactions()
                        analytics.track(
                            QuriAnalyticsEvents.BANK_DEMO_SYNC,
                            mapOf("transactions" to transacciones.size.toString())
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(quriTexto("Probar sincronizacion demo", "Test demo sync"), color = DoradoDinero)
            }
            DemoPreview()
        }
    }
}

@Composable
private fun ProviderRow(provider: BankProvider, onSelect: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.weight(1f)) {
            Text(provider.displayName, color = Color.White, fontWeight = FontWeight.Bold)
            Text(quriTexto("Solo lectura bancaria", "Read-only banking"), color = Color.White.copy(alpha = 0.70f), style = MaterialTheme.typography.bodySmall)
        }
        OutlinedButton(onClick = onSelect) {
            Text(quriTexto("Evaluar", "Evaluate"), color = DoradoDinero)
        }
    }
}

@Composable
private fun DemoPreview() {
    SeparadorQuri()
    FilaDatoQuri("UP", quriTexto("Nomina detectada", "Detected salary"), formatearDineroQuri(130_000L), DoradoDinero)
    FilaDatoQuri("DN", quriTexto("Alquiler detectado", "Detected rent"), formatearDineroQuri(52_000L), Color.White)
}
