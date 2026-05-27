package com.example.quritfg.ui.pantallas.plan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quritfg.datos.di.ModuloApp
import com.example.quritfg.datos.local.IngresoDetectadoEntidad
import com.example.quritfg.datos.local.MetaEntidad
import com.example.quritfg.datos.modelo.FechaQuri
import com.example.quritfg.ui.componentes.BarraProgresoLineaQuri
import com.example.quritfg.ui.componentes.EncabezadoTarjetaQuri
import com.example.quritfg.ui.componentes.FilaDatoQuri
import com.example.quritfg.ui.componentes.SeparadorQuri
import com.example.quritfg.ui.componentes.TarjetaQuri
import com.example.quritfg.ui.componentes.TituloPantallaQuri
import com.example.quritfg.ui.componentes.colorDineroAlerta
import com.example.quritfg.ui.config.formatearDineroQuri
import com.example.quritfg.ui.config.quriTexto
import com.example.quritfg.ui.theme.DoradoDinero
import com.example.quritfg.ui.viewmodels.PlanMensualViewModel
import com.example.quritfg.ui.viewmodels.PlanMensualViewModelFactory
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

@Composable
fun PlanMensualPantalla(navController: NavController) {
    val repositorio = ModuloApp.proporcionarRepositorio(LocalContext.current)
    val vm: PlanMensualViewModel = viewModel(factory = PlanMensualViewModelFactory(repositorio))
    val fondos by vm.fondos.collectAsState(initial = emptyList())
    val ingresos by vm.ingresosDetectados.collectAsState(initial = emptyList())
    val ingresosMes = ingresos.filter { ingresoDelMesActual(it) && !it.deshecho }
    val totalRecibido = ingresosMes.sumOf { it.cantidadCentimos }
    val totalRepartido = ingresosMes.sumOf { it.totalAsignadoCentimos }
    val disponible = (totalRecibido - totalRepartido).coerceAtLeast(0L)
    val fondosRiesgo = fondos.filter { vaEnRiesgo(it) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TituloPantallaQuri(quriTexto("Plan mensual", "Monthly plan"))

        TarjetaQuri {
            EncabezadoTarjetaQuri("PLAN", quriTexto("Resumen del mes", "Month summary"))
            Text(
                quriTexto(
                    "Este mes has recibido ${formatearDineroQuri(totalRecibido)}.",
                    "This month you received ${formatearDineroQuri(totalRecibido)}."
                ),
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            FilaDatoQuri("IN", quriTexto("Ingresos detectados", "Detected income"), formatearDineroQuri(totalRecibido), DoradoDinero)
            SeparadorQuri()
            FilaDatoQuri("Q", quriTexto("Total repartido", "Total assigned"), formatearDineroQuri(totalRepartido), DoradoDinero)
            SeparadorQuri()
            FilaDatoQuri("$", quriTexto("Disponible restante", "Remaining available"), formatearDineroQuri(disponible), Color.White)
        }

        TarjetaQuri {
            EncabezadoTarjetaQuri("SAFE", quriTexto("Fondos actualizados", "Updated funds"))
            if (fondos.isEmpty()) {
                Text(quriTexto("Crea fondos para ver tu plan mensual.", "Create funds to see your monthly plan."), color = Color.White)
            } else {
                fondos.forEach { fondo ->
                    val progreso = progresoFondo(fondo)
                    Text(fondo.nombre, color = Color.White, fontWeight = FontWeight.SemiBold)
                    BarraProgresoLineaQuri(
                        titulo = "${formatearDineroQuri(fondo.cantidadActualCentimos)} ${quriTexto("de", "of")} ${formatearDineroQuri(fondo.cantidadObjetivoCentimos)}",
                        porcentaje = progreso,
                        valorDerecha = "${(progreso * 100).toInt()}%"
                    )
                }
            }
        }

        TarjetaQuri {
            EncabezadoTarjetaQuri("ALERT", quriTexto("Metas en riesgo", "Goals at risk"))
            if (fondosRiesgo.isEmpty()) {
                Text(quriTexto("Tus metas principales van bien este mes.", "Your main goals are on track this month."), color = Color.White)
            } else {
                fondosRiesgo.forEach { fondo ->
                    FilaDatoQuri(
                        "!",
                        fondo.nombre,
                        quriTexto("necesita +${formatearDineroQuri(necesarioMensual(fondo))}/mes", "needs +${formatearDineroQuri(necesarioMensual(fondo))}/month"),
                        colorDineroAlerta()
                    )
                    SeparadorQuri()
                }
            }
        }

        TarjetaQuri {
            EncabezadoTarjetaQuri("Q", quriTexto("Recomendacion de Quri", "Quri recommendation"))
            Text(recomendacionQuri(totalRecibido, totalRepartido, fondos, fondosRiesgo), color = Color.White)
        }

        TarjetaQuri {
            EncabezadoTarjetaQuri("CAL", quriTexto("Calendario de metas", "Goal calendar"))
            val porMes = fondos.groupBy { mesTexto(it.fechaLimite) }
            if (porMes.isEmpty()) {
                Text(quriTexto("No hay fechas limite configuradas.", "No deadlines configured."), color = Color.White)
            } else {
                porMes.forEach { (mes, metas) ->
                    Text(mes, color = DoradoDinero, fontWeight = FontWeight.Bold)
                    Text(metas.joinToString { it.nombre }, color = Color.White)
                    SeparadorQuri()
                }
            }
        }
    }
}

private fun ingresoDelMesActual(ingreso: IngresoDetectadoEntidad): Boolean =
    runCatching {
        YearMonth.from(FechaQuri.parsear(ingreso.fecha)) == YearMonth.now()
    }.getOrDefault(false)

private fun progresoFondo(fondo: MetaEntidad): Float =
    if (fondo.cantidadObjetivoCentimos > 0L) {
        (fondo.cantidadActualCentimos.toFloat() / fondo.cantidadObjetivoCentimos.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

private fun vaEnRiesgo(fondo: MetaEntidad): Boolean =
    progresoFondo(fondo) < progresoTiempo(fondo.fechaLimite)

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
        ChronoUnit.MONTHS.between(
            LocalDate.now().withDayOfMonth(1),
            FechaQuri.parsear(fondo.fechaLimite).withDayOfMonth(1)
        ).coerceAtLeast(1L)
    }.getOrDefault(1L)
    return (restante + meses - 1L) / meses
}

@Composable
private fun recomendacionQuri(
    totalRecibido: Long,
    totalRepartido: Long,
    fondos: List<MetaEntidad>,
    fondosRiesgo: List<MetaEntidad>
): String {
    if (totalRecibido == 0L) {
        return quriTexto("Simula o detecta una nomina para construir tu plan mensual.", "Simulate or detect a salary to build your monthly plan.")
    }
    val fondoRiesgo = fondosRiesgo.firstOrNull()
    val fondoBien = fondos.firstOrNull { !vaEnRiesgo(it) }
    return when {
        fondoRiesgo != null && fondoBien != null -> quriTexto(
            "Quri ha asignado ${formatearDineroQuri(totalRepartido)}. ${fondoBien.nombre} va bien, pero ${fondoRiesgo.nombre} necesita +${formatearDineroQuri(necesarioMensual(fondoRiesgo))}/mes.",
            "Quri assigned ${formatearDineroQuri(totalRepartido)}. ${fondoBien.nombre} is on track, but ${fondoRiesgo.nombre} needs +${formatearDineroQuri(necesarioMensual(fondoRiesgo))}/month."
        )
        fondoRiesgo != null -> quriTexto(
            "${fondoRiesgo.nombre} necesita +${formatearDineroQuri(necesarioMensual(fondoRiesgo))}/mes para llegar a tiempo.",
            "${fondoRiesgo.nombre} needs +${formatearDineroQuri(necesarioMensual(fondoRiesgo))}/month to arrive on time."
        )
        else -> quriTexto("Buen mes: tus fondos van alineados con sus fechas limite.", "Good month: your funds are aligned with their deadlines.")
    }
}

private fun mesTexto(fecha: String): String =
    runCatching {
        val date = FechaQuri.parsear(fecha)
        val meses = listOf("enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre")
        "${meses[date.monthValue - 1]} ${date.year}"
    }.getOrDefault("Sin fecha")
