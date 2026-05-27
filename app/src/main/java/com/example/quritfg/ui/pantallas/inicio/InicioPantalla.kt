package com.example.quritfg.ui.pantallas.inicio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quritfg.datos.di.ModuloApp
import com.example.quritfg.datos.modelo.BancoDemo
import com.example.quritfg.datos.modelo.PlanBancoDemo
import com.example.quritfg.datos.modelo.RepartoFondoBanco
import com.example.quritfg.datos.modelo.ResumenFinanciero
import com.example.quritfg.ui.componentes.colorProgreso
import com.example.quritfg.ui.config.formatearDineroQuri
import com.example.quritfg.ui.config.quriTexto
import com.example.quritfg.ui.theme.DoradoDinero
import com.example.quritfg.ui.viewmodels.InicioViewModel
import com.example.quritfg.ui.viewmodels.InicioViewModelFactory

@Composable
fun InicioPantalla(navController: NavController) {
    val context = LocalContext.current
    val repositorio = ModuloApp.proporcionarRepositorio(context)
    val vm: InicioViewModel = viewModel(
        factory = InicioViewModelFactory(repositorio)
    )

    val fondos by vm.fondos.collectAsState(initial = emptyList())
    val ingresos by vm.listaIngresos.collectAsState(initial = emptyList())
    val gastos by vm.listaGastos.collectAsState(initial = emptyList())
    val resumen by vm.resumenFinanciero.collectAsState(
        initial = ResumenFinanciero(
            totalIngresosCentimos = 0L,
            totalGastosCentimos = 0L,
            ahorroActualCentimos = 0L,
            objetivoCentimos = 0L,
            porcentajeProgreso = 0f,
            ahorroRestanteCentimos = 0L
        )
    )
    val planBanco = BancoDemo.planificarReparto(
        fondos = fondos,
        ingresoDetectadoCentimos = ingresos.maxOfOrNull { it.cantidadCentimos } ?: 130_000L
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TarjetaBancoDemo(planBanco)

        Text(
            text = quriTexto("Resumen", "Summary"),
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White
        )

        TarjetaResumenInicio {
            Text(
                text = quriTexto("Fondos de ahorro", "Savings funds"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text("${quriTexto("Fondos activos", "Active funds")}: ${fondos.size}")
            Text("${quriTexto("Objetivo total", "Total goal")}: ${formatearDineroQuri(resumen.objetivoCentimos)}")
            Text("${quriTexto("Total ingresos", "Total income")}: ${formatearDineroQuri(resumen.totalIngresosCentimos)}")
            Text("${quriTexto("Total gastos", "Total expenses")}: ${formatearDineroQuri(resumen.totalGastosCentimos)}")
            Text("${quriTexto("Ahorrado en fondos", "Saved in funds")}: ${formatearDineroQuri(resumen.ahorroActualCentimos)}")
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MedidorProgresoAhorro(resumen)
            Text(
                text = mensajeProgresoAhorro(resumen.porcentajeProgreso),
                color = DoradoDinero,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TarjetaBancoDemo(plan: PlanBancoDemo) {
    TarjetaResumenInicio {
        Text(
            text = quriTexto("Banco demo conectado", "Demo bank connected"),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "${plan.ingreso.concepto}: ${formatearDineroQuri(plan.ingreso.cantidadCentimos)}",
            color = DoradoDinero,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = quriTexto(
                "Quri propone este reparto virtual sin mover dinero real.",
                "Quri suggests this virtual split without moving real money."
            ),
            color = Color.White.copy(alpha = 0.82f),
            style = MaterialTheme.typography.bodySmall
        )

        if (plan.repartos.isEmpty()) {
            Text(
                text = quriTexto(
                    "Crea fondos con fecha limite para recibir una distribucion automatica.",
                    "Create funds with a deadline to receive an automatic split."
                )
            )
        } else {
            plan.repartos.take(3).forEach { reparto ->
                FilaRepartoBanco(reparto)
            }
            if (plan.repartos.size > 3) {
                Text(
                    text = quriTexto(
                        "+${plan.repartos.size - 3} fondos mas incluidos en el calculo",
                        "+${plan.repartos.size - 3} more funds included in the calculation"
                    ),
                    color = Color.White.copy(alpha = 0.74f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(quriTexto("Asignado", "Assigned"))
            Text(
                text = formatearDineroQuri(plan.totalAsignadoCentimos),
                color = DoradoDinero,
                fontWeight = FontWeight.Bold
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(quriTexto("Resto disponible", "Available rest"))
            Text(
                text = formatearDineroQuri(plan.restanteDisponibleCentimos),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FilaRepartoBanco(reparto: RepartoFondoBanco) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = reparto.fondo.nombre,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = quriTexto(
                    "${reparto.mesesRestantes} meses restantes",
                    "${reparto.mesesRestantes} months left"
                ),
                color = Color.White.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodySmall
            )
        }
        Text(
            text = formatearDineroQuri(reparto.asignadoCentimos),
            color = DoradoDinero,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TarjetaResumenInicio(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x3304160D),
            contentColor = Color.White
        ),
        border = BorderStroke(1.4.dp, DoradoDinero.copy(alpha = 0.72f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}

@Composable
private fun mensajeProgresoAhorro(porcentaje: Float): String {
    val porcentajeTexto = (porcentaje.coerceIn(0f, 1f) * 100).toInt()
    return when {
        porcentajeTexto >= 100 -> quriTexto("Meta conseguida. Gran trabajo.", "Goal reached. Great work.")
        porcentajeTexto >= 70 -> quriTexto("Sigue adelante, estas muy cerca.", "Keep going, you are very close.")
        porcentajeTexto >= 40 -> quriTexto("Sigue adelante, te queda un ${100 - porcentajeTexto}%.", "Keep going, you have ${100 - porcentajeTexto}% left.")
        else -> quriTexto("Hay que trabajar en este ahorro: llevas un $porcentajeTexto%.", "This saving goal needs work: you are at $porcentajeTexto%.")
    }
}

@Composable
private fun MedidorProgresoAhorro(resumen: ResumenFinanciero) {
    val progreso = resumen.porcentajeProgreso.coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        ) {
            val stroke = Stroke(
                width = 22.dp.toPx(),
                cap = StrokeCap.Round
            )
            val horizontalPadding = 44.dp.toPx()
            val topPadding = 22.dp.toPx()
            val arcSize = androidx.compose.ui.geometry.Size(
                width = size.width - horizontalPadding * 2,
                height = (size.width - horizontalPadding * 2)
            )
            val topLeft = androidx.compose.ui.geometry.Offset(horizontalPadding, topPadding)

            drawArc(
                color = Color(0x66349B58),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke
            )

            drawArc(
                color = colorProgreso(progreso),
                startAngle = 180f,
                sweepAngle = 180f * progreso,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${(progreso * 100).toInt()}%",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = quriTexto("del objetivo alcanzado", "of the goal reached"),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.78f)
            )
            Text(
                text = "${formatearDineroQuri(resumen.ahorroActualCentimos)} ${quriTexto("de", "of")} ${formatearDineroQuri(resumen.objetivoCentimos)}",
                style = MaterialTheme.typography.titleMedium,
                color = DoradoDinero,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
