package com.example.quritfg.ui.pantallas.finanzas


import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.quritfg.datos.notifications.QuriHabitNotifier
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quritfg.datos.analytics.LocalAnalyticsTracker
import com.example.quritfg.datos.analytics.QuriAnalyticsEvents
import com.example.quritfg.datos.di.ModuloApp
import com.example.quritfg.datos.local.GastoEntidad
import com.example.quritfg.datos.local.IngresoDetectadoEntidad
import com.example.quritfg.datos.local.IngresoEntidad
import com.example.quritfg.datos.local.MetaEntidad
import com.example.quritfg.datos.modelo.FechaQuri
import com.example.quritfg.datos.modelo.InformeHabitoQuri
import com.example.quritfg.datos.modelo.InsightFinancieroQuri
import com.example.quritfg.datos.modelo.MotorHabitoQuri
import com.example.quritfg.datos.modelo.NivelHabitoQuri
import com.example.quritfg.ui.componentes.BarraProgresoLineaQuri
import com.example.quritfg.ui.componentes.EncabezadoTarjetaQuri
import com.example.quritfg.ui.componentes.FilaDatoQuri
import com.example.quritfg.ui.componentes.IconoCircularQuri
import com.example.quritfg.ui.componentes.SeparadorQuri
import com.example.quritfg.ui.componentes.TarjetaQuri
import com.example.quritfg.ui.componentes.TituloPantallaQuri
import com.example.quritfg.ui.componentes.colorDineroAlerta
import com.example.quritfg.ui.componentes.colorDineroNegativo
import com.example.quritfg.ui.componentes.colorDineroPositivo
import com.example.quritfg.ui.config.formatearDineroQuri
import com.example.quritfg.ui.config.quriTexto
import com.example.quritfg.ui.navegacion.Rutas
import com.example.quritfg.ui.theme.DoradoDinero
import com.example.quritfg.ui.viewmodels.SimulacionBancariaViewModel
import com.example.quritfg.ui.viewmodels.SimulacionBancariaViewModelFactory
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

@Composable
fun FinanzasPantalla(navController: NavController) {
    val context = LocalContext.current
    val repositorio = ModuloApp.proporcionarRepositorio(context)
    val analytics = remember { LocalAnalyticsTracker(context) }
    val habitNotifier = remember { QuriHabitNotifier(context) }
    var notificacionesActivas by remember { mutableStateOf(habitNotifier.canNotify()) }
    val vm: SimulacionBancariaViewModel = viewModel(
        factory = SimulacionBancariaViewModelFactory(repositorio, context, analytics)
    )

    val fondos by vm.fondos.collectAsState(initial = emptyList())
    val historial by vm.historial.collectAsState(initial = emptyList())
    val ingresos by repositorio.obtenerIngresos().collectAsState(initial = emptyList())
    val gastos by repositorio.obtenerGastos().collectAsState(initial = emptyList())
    val informeHabito = remember(ingresos, gastos, fondos) {
        MotorHabitoQuri.analizar(ingresos = ingresos, gastos = gastos, fondos = fondos)
    }

    
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        notificacionesActivas = granted
        if (granted) {
            analytics.track(QuriAnalyticsEvents.HABIT_NOTIFICATIONS_ENABLED)
            habitNotifier.sendHabitPreview(informeHabito.mensajeNotificacion)
            analytics.track(QuriAnalyticsEvents.HABIT_TEST_NOTIFICATION_SENT)
        }
    }
    LaunchedEffect(Unit) {
        analytics.track(QuriAnalyticsEvents.FINANCE_SUMMARY_OPENED)
    }

    PlanMensualFinanzas(
        navController = navController,
        ingresos = ingresos,
        gastos = gastos,
        fondos = fondos,
        historial = historial,
        informeHabito = informeHabito,
        onDeshacer = vm::deshacer,
        notificacionesActivas = notificacionesActivas,
        onActivarHabito = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !habitNotifier.canNotify()) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                notificacionesActivas = true
                analytics.track(QuriAnalyticsEvents.HABIT_NOTIFICATIONS_ENABLED)
                habitNotifier.sendHabitPreview(informeHabito.mensajeNotificacion)
                analytics.track(QuriAnalyticsEvents.HABIT_TEST_NOTIFICATION_SENT)
            }
        }
    )
}

@Composable
private fun PlanMensualFinanzas(
    navController: NavController,
    ingresos: List<IngresoEntidad>,
    gastos: List<GastoEntidad>,
    fondos: List<MetaEntidad>,
    historial: List<IngresoDetectadoEntidad>,
    informeHabito: InformeHabitoQuri,
    onDeshacer: (IngresoDetectadoEntidad) -> Unit,
    notificacionesActivas: Boolean,
    onActivarHabito: () -> Unit
) {
    val ingresosMes = ingresos.filter { esDelMesActual(it.fecha) }
    val gastosMes = gastos.filter { esDelMesActual(it.fecha) }
    val repartosMes = historial.filter { esDelMesActual(it.fecha) && !it.deshecho }
    val totalIngresos = ingresosMes.sumOf { it.cantidadCentimos }
    val totalGastos = gastosMes.sumOf { it.cantidadCentimos }
    val totalRepartido = repartosMes.sumOf { it.totalAsignadoCentimos }
    val disponible = (totalIngresos - totalGastos - totalRepartido).coerceAtLeast(0L)
    val fondosRiesgo = fondos.filter { vaEnRiesgo(it) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TituloPantallaQuri(quriTexto("Plan mensual", "Monthly plan"))
        Text(
            text = quriTexto(
                "Finanzas sirve para decidir que hacer este mes. Analisis queda para ver graficos y tendencias.",
                "Finance is for deciding what to do this month. Analysis is for charts and trends."
            ),
            color = Color.White.copy(alpha = 0.80f),
            style = MaterialTheme.typography.bodyMedium
        )

        TarjetaQuri {
            EncabezadoTarjetaQuri("PLAN", quriTexto("Resumen operativo", "Operational summary"))
            if (ingresosMes.isEmpty()) {
                Text(quriTexto("Todavia no has registrado ingresos este mes.", "You have not registered income this month yet."), color = Color.White)
            }
            FilaDatoQuri("UP", quriTexto("Ingresos del mes", "Monthly income"), formatearDineroQuri(totalIngresos), colorDineroPositivo())
            SeparadorQuri()
            FilaDatoQuri("DN", quriTexto("Gastos del mes", "Monthly expenses"), formatearDineroQuri(totalGastos), colorDineroNegativo())
            SeparadorQuri()
            FilaDatoQuri("PIG", quriTexto("Repartido a fondos", "Assigned to funds"), formatearDineroQuri(totalRepartido), DoradoDinero)
            SeparadorQuri()
            FilaDatoQuri("$", quriTexto("Disponible estimado", "Estimated available"), formatearDineroQuri(disponible), Color.White)
        }

        TarjetaQuri {
            EncabezadoTarjetaQuri("Q", quriTexto("Decision de este mes", "This month decision"))
            Text(recomendacionPlan(totalIngresos, totalGastos, totalRepartido, disponible, fondosRiesgo), color = Color.White)
            Button(
                onClick = { navController.navigate(Rutas.AnadirGasto.ruta) { launchSingleTop = true } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DoradoDinero, contentColor = Color(0xFF06160D))
            ) {
                Text(quriTexto("Anadir ingreso o gasto", "Add income or expense"), fontWeight = FontWeight.Bold)
            }
        }
        TarjetaQuri {
            EncabezadoTarjetaQuri("IA", quriTexto("Fase 3: inteligencia financiera", "Phase 3: financial intelligence"))
            FilaDatoQuri(
                "OBJ",
                quriTexto("Salud financiera", "Financial health"),
                "${informeHabito.saludFinanciera}/100",
                colorSaludFinanciera(informeHabito.saludFinanciera)
            )
            Text(
                text = informeHabito.resumenInteligente,
                color = Color.White.copy(alpha = 0.86f),
                style = MaterialTheme.typography.bodyMedium
            )
            SeparadorQuri()
            if (informeHabito.insights.isEmpty()) {
                Text(quriTexto("Aun no hay suficientes datos comparativos para detectar patrones.", "There is not enough comparative data to detect patterns yet."), color = Color.White)
            } else {
                informeHabito.insights.take(4).forEach { insight ->
                    InsightFinancieroFila(insight)
                    SeparadorQuri()
                }
            }
        }

        TarjetaQuri {
            EncabezadoTarjetaQuri("IA", quriTexto("Recomendaciones inteligentes", "Smart recommendations"))
            if (informeHabito.recomendaciones.isEmpty()) {
                Text(quriTexto("Quri no detecta riesgos importantes ahora mismo.", "Quri does not detect major risks right now."), color = Color.White)
            } else {
                informeHabito.recomendaciones.take(4).forEach { recomendacion ->
                    RecomendacionHabitoFila(recomendacion)
                    SeparadorQuri()
                }
            }
        }


        TarjetaQuri {
            EncabezadoTarjetaQuri("CLK", quriTexto("Fase 2: habito", "Phase 2: habit"))
            Text(
                quriTexto(
                    "Activa recordatorios inteligentes para que Quri te avise cuando haya que revisar el plan mensual o reforzar un fondo.",
                    "Enable smart reminders so Quri can warn you when it is time to review the monthly plan or reinforce a fund."
                ),
                color = Color.White
            )
            FilaDatoQuri(
                "OK",
                quriTexto("Estado", "Status"),
                if (notificacionesActivas) quriTexto("Activadas", "Enabled") else quriTexto("Pendientes", "Pending"),
                DoradoDinero
            )
            Button(
                onClick = onActivarHabito,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DoradoDinero, contentColor = Color(0xFF06160D))
            ) {
                Text(quriTexto("Activar recordatorios inteligentes", "Enable smart reminders"), fontWeight = FontWeight.Bold)
            }
        }
        TarjetaQuri {
            EncabezadoTarjetaQuri("BANK", quriTexto("Preparar banco real", "Prepare real bank"))
            Text(
                quriTexto(
                    "La beta ya se entiende. El siguiente paso es medir interes por conectar banco en modo solo lectura.",
                    "The beta is understood. The next step is measuring interest in read-only bank connection."
                ),
                color = Color.White
            )
            Button(
                onClick = { navController.navigate(Rutas.Banco.ruta) { launchSingleTop = true } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DoradoDinero, contentColor = Color(0xFF06160D))
            ) {
                Text(quriTexto("Ver preparacion Fase 1", "View Phase 1 setup"), fontWeight = FontWeight.Bold)
            }
        }
        TarjetaQuri {
            EncabezadoTarjetaQuri("SAFE", quriTexto("Fondos a atender", "Funds needing attention"))
            if (fondos.isEmpty()) {
                Text(quriTexto("Crea fondos para que Quri pueda priorizar tu reparto mensual.", "Create funds so Quri can prioritize your monthly split."), color = Color.White)
            } else if (fondosRiesgo.isEmpty()) {
                Text(quriTexto("No hay fondos en riesgo. Puedes mantener el reparto actual.", "No funds are at risk. You can keep the current split."), color = Color.White)
            } else {
                fondosRiesgo.take(4).forEach { fondo ->
                    FondoPlanFila(fondo)
                    SeparadorQuri()
                }
            }
        }

        TarjetaQuri {
            EncabezadoTarjetaQuri("CAL", quriTexto("Proximas fechas", "Upcoming deadlines"))
            if (fondos.isEmpty()) {
                Text(quriTexto("No hay fechas limite configuradas.", "No deadlines configured."), color = Color.White)
            } else {
                fondos.sortedBy { fechaOrden(it.fechaLimite) }.take(4).forEach { fondo ->
                    val dias = diasRestantes(fondo.fechaLimite)?.toString() ?: "?"
                    FilaDatoQuri("CAL", fondo.nombre, FechaQuri.mostrar(fondo.fechaLimite), DoradoDinero, "$dias dias")
                    SeparadorQuri()
                }
            }
        }

        TarjetaQuri {
            EncabezadoTarjetaQuri("HIST", quriTexto("Ultimos repartos", "Latest splits"))
            if (historial.isEmpty()) {
                Text(quriTexto("Cuando confirmes un reparto aparecera aqui.", "When you confirm a split it will appear here."), color = Color.White)
            } else {
                historial.take(5).forEach { ingreso ->
                    Text(
                        text = "${ingreso.concepto} - ${formatearDineroQuri(ingreso.cantidadCentimos)}${if (ingreso.deshecho) " - deshecho" else ""}",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(ingreso.repartoResumen, color = Color.White.copy(alpha = 0.72f), style = MaterialTheme.typography.bodySmall)
                    if (ingreso.confirmado && !ingreso.deshecho) {
                        TextButton(onClick = { onDeshacer(ingreso) }) {
                            Text(quriTexto("Deshacer reparto", "Undo split"), color = DoradoDinero)
                        }
                    }
                    SeparadorQuri()
                }
            }
        }
    }
}

@Composable
private fun InsightFinancieroFila(insight: InsightFinancieroQuri) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        IconoCircularQuri(icono = iconoHabito(insight.nivel), size = 42)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = insight.titulo,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                insight.metrica?.let { metrica ->
                    Text(
                        text = metrica,
                        color = colorHabito(insight.nivel),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                text = insight.detalle,
                color = Color.White.copy(alpha = 0.78f),
                style = MaterialTheme.typography.bodyMedium
            )
            insight.explicacion?.let { explicacion ->
                Text(
                    text = quriTexto("Por que: $explicacion", "Why: $explicacion"),
                    color = DoradoDinero.copy(alpha = 0.86f),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun colorSaludFinanciera(salud: Int): Color =
    when {
        salud >= 75 -> colorDineroPositivo()
        salud >= 50 -> DoradoDinero
        else -> colorDineroNegativo()
    }

@Composable
private fun RecomendacionHabitoFila(recomendacion: com.example.quritfg.datos.modelo.RecomendacionHabitoQuri) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        IconoCircularQuri(icono = iconoHabito(recomendacion.nivel), size = 42)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = recomendacion.titulo,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = recomendacion.detalle,
                color = colorHabito(recomendacion.nivel),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun FondoPlanFila(fondo: MetaEntidad) {
    val progreso = progresoFondo(fondo)
    val necesario = necesarioMensual(fondo)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(fondo.nombre, color = Color.White, fontWeight = FontWeight.Bold)
            Text(quriTexto("+${formatearDineroQuri(necesario)}/mes", "+${formatearDineroQuri(necesario)}/month"), color = colorDineroAlerta(), fontWeight = FontWeight.Bold)
        }
        BarraProgresoLineaQuri(
            titulo = "${formatearDineroQuri(fondo.cantidadActualCentimos)} ${quriTexto("de", "of")} ${formatearDineroQuri(fondo.cantidadObjetivoCentimos)}",
            porcentaje = progreso,
            valorDerecha = "${(progreso * 100).toInt()}%"
        )
    }
}

@Composable
private fun recomendacionPlan(
    totalIngresos: Long,
    totalGastos: Long,
    totalRepartido: Long,
    disponible: Long,
    fondosRiesgo: List<MetaEntidad>
): String {
    if (totalIngresos <= 0L) {
        return quriTexto("Registra tu salario en Anadir para que Quri calcule el plan mensual.", "Register your salary in Add so Quri can calculate the monthly plan.")
    }
    val fondo = fondosRiesgo.firstOrNull()
    return when {
        fondo != null -> quriTexto(
            "Este mes quedan ${formatearDineroQuri(disponible)} disponibles. ${fondo.nombre} necesita ${formatearDineroQuri(necesarioMensual(fondo))}/mes para llegar a tiempo.",
            "This month ${formatearDineroQuri(disponible)} remains available. ${fondo.nombre} needs ${formatearDineroQuri(necesarioMensual(fondo))}/month to arrive on time."
        )
        totalRepartido <= 0L -> quriTexto(
            "Has registrado ingresos, pero aun no has repartido dinero a fondos. Haz el reparto desde Anadir ingreso.",
            "You registered income, but have not assigned money to funds yet. Split it from Add income."
        )
        totalGastos > totalIngresos -> quriTexto(
            "Tus gastos superan tus ingresos este mes. Conviene revisar gastos antes de aumentar fondos.",
            "Your expenses exceed income this month. Review expenses before increasing funds."
        )
        else -> quriTexto(
            "El mes esta controlado. Puedes mantener el reparto actual y revisar Analisis para ver tendencias.",
            "The month is under control. You can keep the current split and review Analysis for trends."
        )
    }
}

@Composable
private fun colorHabito(nivel: NivelHabitoQuri): Color =
    when (nivel) {
        NivelHabitoQuri.POSITIVO -> colorDineroPositivo()
        NivelHabitoQuri.AVISO -> DoradoDinero
        NivelHabitoQuri.RIESGO -> colorDineroNegativo()
    }

private fun iconoHabito(nivel: NivelHabitoQuri): String =
    when (nivel) {
        NivelHabitoQuri.POSITIVO -> "OK"
        NivelHabitoQuri.AVISO -> "!"
        NivelHabitoQuri.RIESGO -> "AL"
    }

private fun esDelMesActual(fecha: String): Boolean =
    runCatching { YearMonth.from(FechaQuri.parsear(fecha)) == YearMonth.now() }.getOrDefault(false)

private fun fechaOrden(fecha: String): LocalDate =
    runCatching { FechaQuri.parsear(fecha) }.getOrDefault(LocalDate.MAX)

private fun progresoFondo(fondo: MetaEntidad): Float =
    if (fondo.cantidadObjetivoCentimos > 0L) {
        (fondo.cantidadActualCentimos.toFloat() / fondo.cantidadObjetivoCentimos.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

private fun vaEnRiesgo(fondo: MetaEntidad): Boolean = progresoFondo(fondo) < progresoTiempo(fondo.fechaLimite)

private fun progresoTiempo(fechaLimite: String): Float {
    val limite = runCatching { FechaQuri.parsear(fechaLimite) }.getOrNull() ?: return 0f
    val hoy = LocalDate.now()
    val referencia = limite.minusYears(1)
    val totalDias = ChronoUnit.DAYS.between(referencia, limite).coerceAtLeast(1L)
    val diasConsumidos = ChronoUnit.DAYS.between(referencia, hoy).coerceIn(0L, totalDias)
    return (diasConsumidos.toFloat() / totalDias.toFloat()).coerceIn(0f, 1f)
}

private fun necesarioMensual(fondo: MetaEntidad): Long {
    val restante = (fondo.cantidadObjetivoCentimos - fondo.cantidadActualCentimos).coerceAtLeast(0L)
    val meses = runCatching {
        ChronoUnit.MONTHS.between(LocalDate.now().withDayOfMonth(1), FechaQuri.parsear(fondo.fechaLimite).withDayOfMonth(1)).coerceAtLeast(1L)
    }.getOrDefault(1L)
    return (restante + meses - 1L) / meses
}

private fun diasRestantes(fechaLimite: String): Long? =
    runCatching { ChronoUnit.DAYS.between(LocalDate.now(), FechaQuri.parsear(fechaLimite)).coerceAtLeast(0L) }.getOrNull()












