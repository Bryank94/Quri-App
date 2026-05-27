package com.example.quritfg.ui.pantallas.progreso

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.quritfg.datos.SesionManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quritfg.datos.di.ModuloApp
import com.example.quritfg.datos.local.GastoEntidad
import com.example.quritfg.datos.local.IngresoEntidad
import com.example.quritfg.datos.local.MetaEntidad
import com.example.quritfg.datos.modelo.FechaQuri
import com.example.quritfg.datos.modelo.RecompensasQuri
import com.example.quritfg.datos.modelo.ResumenFinanciero
import com.example.quritfg.ui.componentes.IconoCircularQuri
import com.example.quritfg.ui.componentes.SeparadorQuri
import com.example.quritfg.ui.config.formatearDineroQuri
import com.example.quritfg.ui.config.quriTexto
import com.example.quritfg.ui.theme.DoradoDinero
import com.example.quritfg.ui.theme.RojoPeligro
import com.example.quritfg.ui.theme.VerdeDinero
import com.example.quritfg.ui.viewmodels.ProgresoViewModel
import com.example.quritfg.ui.viewmodels.ProgresoViewModelFactory
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private val VerdeQuri = Color(0xFF38D665)
private val VerdeBrillo = Color(0xFF9BFF76)
private val AzulQuri = Color(0xFF26B7FF)
private val NaranjaQuri = Color(0xFFFF744D)
private val MoradoQuri = Color(0xFF9B6DFF)
private val PanelAnalisis = Color(0xAA04180D)
private val BordeAnalisis = DoradoDinero.copy(alpha = 0.62f)

@Composable
fun ProgresoPantalla(navController: NavController) {
    val context = LocalContext.current
    val repositorio = ModuloApp.proporcionarRepositorio(context)
    val vm: ProgresoViewModel = viewModel(
        factory = ProgresoViewModelFactory(repositorio)
    )

    val fondos by vm.fondos.collectAsState(initial = emptyList())
    val gastos by vm.listaGastos.collectAsState(initial = emptyList())
    val ingresos by vm.listaIngresos.collectAsState(initial = emptyList())
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

    AnalisisDashboard(
        ingresos = ingresos,
        gastos = gastos,
        fondos = fondos,
        resumen = resumen
    )
}

@Composable
private fun AnalisisDashboard(
    ingresos: List<IngresoEntidad>,
    gastos: List<GastoEntidad>,
    fondos: List<MetaEntidad>,
    resumen: ResumenFinanciero
) {
    val puntos = RecompensasQuri.calcularPuntos(ingresos, gastos, fondos)
    val balance = resumen.totalIngresosCentimos - resumen.totalGastosCentimos
    val distribucion = distribucionGastos(gastos)
    val calidad = calidadGasto(gastos)
    val fondoPrincipal = fondos.maxByOrNull { it.cantidadObjetivoCentimos }
    val fondosUrgentes = fondos.sortedBy { runCatching { FechaQuri.parsear(it.fechaLimite) }.getOrDefault(LocalDate.MAX) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, top = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 112.dp)
    ) {
        item { CabeceraAnalisis(puntos = puntos) }
        item { BalanceAnalisisCard(balance = balance) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MiniMetricaCard(quriTexto("Ingresos", "Income"), formatearDineroQuri(resumen.totalIngresosCentimos), "UP", VerdeQuri, "+ 8,2%", Modifier.weight(1f))
                MiniMetricaCard(quriTexto("Gastos", "Expenses"), formatearDineroQuri(resumen.totalGastosCentimos), "DN", RojoPeligro, "- 5,1%", Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MiniMetricaCard(quriTexto("Ahorro", "Savings"), formatearDineroQuri(resumen.ahorroActualCentimos), "PIG", DoradoDinero, "+ 15,3%", Modifier.weight(1f))
                MiniMetricaCard(quriTexto("Objetivo", "Goal"), formatearDineroQuri(resumen.objetivoCentimos), "OBJ", AzulQuri, "${(resumen.porcentajeProgreso * 100).toInt()}%", Modifier.weight(1f))
            }
        }
        item { DistribucionGastosCard(distribucion = distribucion, total = resumen.totalGastosCentimos) }
        item { InsightIaCard(distribucion = distribucion, fondos = fondos) }
        item { ProgresoObjetivoCard(resumen = resumen, fondo = fondoPrincipal) }
        item { FondosActivosCard(fondos = fondos) }
        item { FechasLimiteCard(fondos = fondosUrgentes) }
        item { CalidadGastoCard(calidad = calidad) }
        item { TendenciaCard() }
        item { LogrosCard(puntos = puntos) }
    }
}

@Composable
private fun CabeceraAnalisis(puntos: Int) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("quri_user_menu", android.content.Context.MODE_PRIVATE) }
    val sesionManager = remember { SesionManager(context) }
    val nombrePerfil = prefs.getString("perfil_nombre", "")?.trim().orEmpty()
    val emailSesion = sesionManager.obtenerEmailUsuario().orEmpty()
    val nombreSaludo = nombrePerfil.ifBlank {
        emailSesion.substringBefore('@').takeIf { it.isNotBlank() } ?: quriTexto("usuario", "user")
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        IconoCircularQuri(icono = "Q", size = 58)
        Column(modifier = Modifier.weight(1f)) {
            Text(quriTexto("Hola, $nombreSaludo", "Hi, $nombreSaludo"), color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(quriTexto("Nivel 12 - Ahorrador Elite", "Level 12 - Elite saver"), color = DoradoDinero, fontWeight = FontWeight.Bold)
        }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0x6606140A))
                .border(1.dp, DoradoDinero.copy(alpha = 0.70f), RoundedCornerShape(28.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("$puntos pts", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            IconoCircularQuri(icono = "Q", size = 34)
        }
    }
}

@Composable
private fun AnalisisCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PanelAnalisis, contentColor = Color.White),
        border = BorderStroke(1.dp, BordeAnalisis),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0x222AD267),
                            Color.Transparent,
                            DoradoDinero.copy(alpha = 0.13f)
                        )
                    )
                )
                .padding(14.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun BalanceAnalisisCard(balance: Long) {
    AnalisisCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(quriTexto("Balance total", "Total balance"), color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(quriTexto("Este mes", "This month"), color = DoradoDinero, modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, DoradoDinero.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 7.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(0.72f)) {
                    Text(formatearDineroQuri(balance), color = if (balance >= 0) VerdeQuri else RojoPeligro, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black)
                    Text("▲ 12,5%", color = VerdeQuri, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                    Text(quriTexto("vs. mes anterior", "vs. previous month"), color = Color.White.copy(alpha = 0.78f))
                }
                LineaBalance(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MiniMetricaCard(
    titulo: String,
    valor: String,
    icono: String,
    color: Color,
    variacion: String,
    modifier: Modifier = Modifier
) {
    AnalisisCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
            IconoCircularQuri(icono = icono, size = 42)
            Text(titulo, color = Color.White, style = MaterialTheme.typography.bodyLarge)
            Text(valor, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(variacion, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DistribucionGastosCard(distribucion: List<DistribucionGasto>, total: Long) {
    AnalisisCard {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(quriTexto("Distribucion de gastos", "Expense distribution"), color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            if (total <= 0L) {
                Text(quriTexto("Todavia no hay gastos registrados.", "No expenses registered yet."), color = Color.White.copy(alpha = 0.78f))
            } else {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(0.90f)) {
                        DonutChart(distribucion = distribucion, total = total, size = 190)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(quriTexto("Total", "Total"), color = Color.White.copy(alpha = 0.78f))
                            Text(formatearDineroQuri(total), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        distribucion.take(5).forEach { item ->
                            LeyendaGasto(item)
                        }
                    }
                }
                TextButton(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DoradoDinero.copy(alpha = 0.55f), RoundedCornerShape(18.dp))
                ) { Text(quriTexto("Ver analisis completo", "View full analysis"), color = DoradoDinero, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun InsightIaCard(distribucion: List<DistribucionGasto>, fondos: List<MetaEntidad>) {
    val mayor = distribucion.firstOrNull()
    val fondo = fondos.firstOrNull { vaEnRiesgo(it) } ?: fondos.firstOrNull()
    AnalisisCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(quriTexto("Insight de IA", "AI insight"), color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, DoradoDinero.copy(alpha = 0.40f), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = mayor?.let { quriTexto("Estas gastando mas en ${it.categoria}.", "You are spending more on ${it.categoria}.") }
                            ?: quriTexto("Anade gastos para obtener recomendaciones.", "Add expenses to get recommendations."),
                        color = DoradoDinero,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    SeparadorQuri()
                    Text(
                        text = fondo?.let { quriTexto("Si reduces 80 euros/mes, ${it.nombre} puede llegar antes.", "If you reduce 80 euros/month, ${it.nombre} can arrive sooner.") }
                            ?: quriTexto("Crea fondos para medir el impacto del ahorro.", "Create funds to measure saving impact."),
                        color = Color.White
                    )
                }
            }
            TextButton(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DoradoDinero.copy(alpha = 0.55f), RoundedCornerShape(18.dp))
            ) { Text(quriTexto("Ver recomendaciones", "View recommendations"), color = DoradoDinero, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun ProgresoObjetivoCard(resumen: ResumenFinanciero, fondo: MetaEntidad?, modifier: Modifier = Modifier) {
    AnalisisCard(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(quriTexto("Progreso objetivo", "Goal progress"), color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            Box(contentAlignment = Alignment.Center) {
                CircularProgreso(resumen.porcentajeProgreso, size = 108, stroke = 12f)
                Text("${(resumen.porcentajeProgreso * 100).toInt()}%", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            }
            Text("${formatearDineroQuri(resumen.ahorroActualCentimos)} / ${formatearDineroQuri(resumen.objetivoCentimos)}", color = DoradoDinero, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(quriTexto("Fecha limite", "Deadline") + ": ${fondo?.fechaLimite?.let { FechaQuri.mostrar(it) } ?: "--"}", color = Color.White.copy(alpha = 0.82f), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun FondosActivosCard(fondos: List<MetaEntidad>, modifier: Modifier = Modifier) {
    AnalisisCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(quriTexto("Fondos activos", "Active funds"), color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(quriTexto("Ver todos", "View all"), color = DoradoDinero, style = MaterialTheme.typography.bodySmall)
            }
            if (fondos.isEmpty()) {
                Text(quriTexto("Crea tu primer fondo.", "Create your first fund."), color = Color.White.copy(alpha = 0.78f))
            } else {
                fondos.take(2).forEach { fondo ->
                    FondoLinea(fondo)
                    SeparadorQuri()
                }
            }
        }
    }
}

@Composable
private fun FechasLimiteCard(fondos: List<MetaEntidad>, modifier: Modifier = Modifier) {
    AnalisisCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(quriTexto("Fondos y fechas limite", "Funds and deadlines"), color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (fondos.isEmpty()) {
                Text(quriTexto("Sin fechas limite todavia.", "No deadlines yet."), color = Color.White.copy(alpha = 0.78f))
            } else {
                fondos.take(2).forEach { fondo ->
                    val restante = (fondo.cantidadObjetivoCentimos - fondo.cantidadActualCentimos).coerceAtLeast(0L)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        IconoCircularQuri("CAL", 38)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(fondo.nombre, color = Color.White, fontWeight = FontWeight.Bold)
                            BarraMini(progresoFondo(fondo), DoradoDinero)
                            Text(quriTexto("Fecha", "Date") + ": ${FechaQuri.mostrar(fondo.fechaLimite)}", color = Color.White.copy(alpha = 0.78f), style = MaterialTheme.typography.bodySmall)
                        }
                        Text(formatearDineroQuri(restante), color = DoradoDinero, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun CalidadGastoCard(calidad: CalidadGasto, modifier: Modifier = Modifier) {
    val total = (calidad.necesario + calidad.importante + calidad.innecesario).coerceAtLeast(1L)
    val progresoBueno = ((calidad.necesario + calidad.importante).toFloat() / total.toFloat()).coerceIn(0f, 1f)
    AnalisisCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(quriTexto("Calidad del gasto", "Expense quality"), color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgreso(progresoBueno, size = 96, stroke = 12f)
                    Text(quriTexto("Buen\navance", "Good\nprogress"), color = Color.White, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall)
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    CalidadFila(quriTexto("Necesario", "Necessary"), calidad.necesario, VerdeQuri)
                    CalidadFila(quriTexto("Importante", "Important"), calidad.importante, DoradoDinero)
                    CalidadFila(quriTexto("Innecesario", "Unnecessary"), calidad.innecesario, NaranjaQuri)
                }
            }
            Text(quriTexto("Aqui puedes haber margen para acelerar tus fondos.", "There may be room here to speed up your funds."), color = Color.White.copy(alpha = 0.82f), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun TendenciaCard(modifier: Modifier = Modifier) {
    AnalisisCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(quriTexto("Tendencia financiera", "Financial trend"), color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(quriTexto("Este mes", "This month"), color = DoradoDinero, style = MaterialTheme.typography.bodySmall)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LeyendaLinea(VerdeQuri, quriTexto("Ingresos", "Income"))
                LeyendaLinea(NaranjaQuri, quriTexto("Gastos", "Expenses"))
            }
            TendenciaCanvas()
        }
    }
}

@Composable
private fun LogrosCard(puntos: Int, modifier: Modifier = Modifier) {
    AnalisisCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(quriTexto("Logros recientes", "Recent achievements"), color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(quriTexto("Ver todos", "View all"), color = DoradoDinero, style = MaterialTheme.typography.bodySmall)
            }
            LogroFila("Q", quriTexto("Racha de ahorro", "Saving streak"), quriTexto("15 dias consecutivos", "15 consecutive days"), "+50 pts")
            LogroFila("OBJ", quriTexto("Objetivo en camino", "Goal on track"), quriTexto("Vas por buen camino", "You are on track"), "+30 pts")
            LogroFila("UP", quriTexto("Gasto inteligente", "Smart spending"), quriTexto("Puntos actuales", "Current points"), "$puntos pts")
        }
    }
}

@Composable
private fun FondoLinea(fondo: MetaEntidad) {
    val progreso = progresoFondo(fondo)
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        IconoCircularQuri("SAFE", 42)
        Column(modifier = Modifier.weight(1f)) {
            Text("Fondo: ${fondo.nombre}", color = Color.White)
            BarraMini(progreso, DoradoDinero)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(formatearDineroQuri(fondo.cantidadActualCentimos), color = DoradoDinero, fontWeight = FontWeight.Bold)
            Text("${(progreso * 100).toInt()}%", color = Color.White)
        }
    }
}

@Composable
private fun LeyendaGasto(item: DistribucionGasto) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(item.color))
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.categoria, color = Color.White)
            Text("${item.porcentaje}%", color = Color.White.copy(alpha = 0.65f), style = MaterialTheme.typography.bodySmall)
        }
        Text(formatearDineroQuri(item.total), color = Color.White)
    }
}

@Composable
private fun CalidadFila(label: String, valor: Long, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(9.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(7.dp))
        Text(label, color = Color.White, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
        Text(formatearDineroQuri(valor), color = color, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun LogroFila(icono: String, titulo: String, detalle: String, puntos: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        IconoCircularQuri(icono, 38)
        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            Text(detalle, color = Color.White.copy(alpha = 0.72f), style = MaterialTheme.typography.bodySmall)
        }
        Text(puntos, color = VerdeBrillo, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun BarraMini(progreso: Float, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(9.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xDD021008))
            .border(1.dp, color.copy(alpha = 0.20f), RoundedCornerShape(20.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progreso.coerceIn(0f, 1f))
                .height(9.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFFFFF2A0), color, Color.White.copy(alpha = 0.75f))))
        )
    }
}

@Composable
private fun DonutChart(distribucion: List<DistribucionGasto>, total: Long, size: Int) {
    Canvas(modifier = Modifier.size(size.dp)) {
        val stroke = Stroke(width = 31f, cap = StrokeCap.Butt)
        var start = -90f
        distribucion.forEach { item ->
            val sweep = if (total > 0L) 360f * item.total.toFloat() / total.toFloat() else 0f
            drawArc(item.color, start, sweep, useCenter = false, style = stroke)
            start += sweep
        }
    }
}

@Composable
private fun CircularProgreso(progreso: Float, size: Int, stroke: Float) {
    Canvas(modifier = Modifier.size(size.dp)) {
        drawArc(Color(0x4436D260), -90f, 360f, false, style = Stroke(width = stroke, cap = StrokeCap.Round))
        drawArc(
            brush = Brush.sweepGradient(listOf(DoradoDinero, VerdeQuri, VerdeBrillo, DoradoDinero)),
            startAngle = -90f,
            sweepAngle = 360f * progreso.coerceIn(0f, 1f),
            useCenter = false,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun LineaBalance(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxWidth().height(120.dp)) {
        val puntos = listOf(0.25f, 0.34f, 0.48f, 0.42f, 0.70f, 0.58f, 0.52f, 0.66f, 0.82f, 0.74f, 0.90f)
        val step = size.width / (puntos.size - 1)
        for (index in 0 until puntos.lastIndex) {
            drawLine(
                color = VerdeBrillo,
                start = Offset(index * step, size.height * (1f - puntos[index])),
                end = Offset((index + 1) * step, size.height * (1f - puntos[index + 1])),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }
        puntos.forEachIndexed { index, point ->
            drawCircle(Color.White, radius = 5f, center = Offset(index * step, size.height * (1f - point)))
            drawCircle(VerdeBrillo, radius = 3f, center = Offset(index * step, size.height * (1f - point)))
        }
    }
}

@Composable
private fun TendenciaCanvas() {
    Canvas(modifier = Modifier.fillMaxWidth().height(145.dp)) {
        val ingresos = listOf(0.30f, 0.38f, 0.55f, 0.70f, 0.73f, 0.70f, 0.78f, 0.75f, 0.90f, 0.94f)
        val gastos = listOf(0.08f, 0.14f, 0.16f, 0.30f, 0.40f, 0.32f, 0.38f, 0.31f, 0.38f, 0.42f)
        val step = size.width / (ingresos.size - 1)
        listOf(0.25f, 0.50f, 0.75f).forEach { y ->
            drawLine(Color.White.copy(alpha = 0.10f), Offset(0f, size.height * y), Offset(size.width, size.height * y), 1f)
        }
        for (i in 0 until ingresos.lastIndex) {
            drawLine(VerdeQuri, Offset(i * step, size.height * (1f - ingresos[i])), Offset((i + 1) * step, size.height * (1f - ingresos[i + 1])), strokeWidth = 4f, cap = StrokeCap.Round)
            drawLine(NaranjaQuri, Offset(i * step, size.height * (1f - gastos[i])), Offset((i + 1) * step, size.height * (1f - gastos[i + 1])), strokeWidth = 4f, cap = StrokeCap.Round)
        }
    }
}

@Composable
private fun LeyendaLinea(color: Color, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        Box(Modifier.size(9.dp).clip(CircleShape).background(color))
        Text(texto, color = Color.White, style = MaterialTheme.typography.bodySmall)
    }
}

private data class DistribucionGasto(
    val categoria: String,
    val total: Long,
    val porcentaje: Int,
    val color: Color
)

private data class CalidadGasto(
    val necesario: Long,
    val importante: Long,
    val innecesario: Long
)

private fun distribucionGastos(gastos: List<GastoEntidad>): List<DistribucionGasto> {
    val total = gastos.sumOf { it.cantidadCentimos }.coerceAtLeast(1L)
    val colores = listOf(VerdeQuri, DoradoDinero, NaranjaQuri, AzulQuri, MoradoQuri)
    return gastos
        .groupBy { it.categoria }
        .mapValues { (_, lista) -> lista.sumOf { it.cantidadCentimos } }
        .toList()
        .sortedByDescending { it.second }
        .take(5)
        .mapIndexed { index, (categoria, cantidad) ->
            DistribucionGasto(
                categoria = categoria,
                total = cantidad,
                porcentaje = ((cantidad.toDouble() / total.toDouble()) * 100).toInt(),
                color = colores[index % colores.size]
            )
        }
}

private fun calidadGasto(gastos: List<GastoEntidad>): CalidadGasto =
    CalidadGasto(
        necesario = gastos.filter { it.etiqueta == "Necesario" }.sumOf { it.cantidadCentimos },
        importante = gastos.filter { it.etiqueta == "Importante" }.sumOf { it.cantidadCentimos },
        innecesario = gastos.filter { it.etiqueta == "Innecesario" }.sumOf { it.cantidadCentimos }
    )

private fun progresoFondo(fondo: MetaEntidad): Float =
    if (fondo.cantidadObjetivoCentimos > 0L) {
        (fondo.cantidadActualCentimos.toFloat() / fondo.cantidadObjetivoCentimos.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

private fun diasRestantes(fechaLimite: String): Long? =
    try {
        ChronoUnit.DAYS.between(LocalDate.now(), FechaQuri.parsear(fechaLimite)).coerceAtLeast(0L)
    } catch (_: Exception) {
        null
    }

private fun progresoTiempo(fechaLimite: String): Float {
    val limite = try {
        FechaQuri.parsear(fechaLimite)
    } catch (_: Exception) {
        return 0f
    }
    val hoy = LocalDate.now()
    val referencia = limite.minusYears(1)
    val totalDias = ChronoUnit.DAYS.between(referencia, limite).coerceAtLeast(1L)
    val diasConsumidos = ChronoUnit.DAYS.between(referencia, hoy).coerceIn(0L, totalDias)
    return (diasConsumidos.toDouble() / totalDias.toDouble()).toFloat()
}

private fun vaEnRiesgo(fondo: MetaEntidad): Boolean = progresoFondo(fondo) < progresoTiempo(fondo.fechaLimite)



