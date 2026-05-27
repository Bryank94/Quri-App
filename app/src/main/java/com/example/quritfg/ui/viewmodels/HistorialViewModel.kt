package com.example.quritfg.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quritfg.datos.local.GastoEntidad
import com.example.quritfg.datos.local.IngresoEntidad
import com.example.quritfg.datos.modelo.FechaQuri
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom
import com.example.quritfg.ui.modelo.HistorialMes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.util.Locale

/**
 * ViewModel del historial.
 *
 * Se encarga de transformar los gastos en una estructura
 * agrupada por meses para la UI.
 */
class HistorialViewModel(
    private val repositorio: RepositorioQuriRoom
) : ViewModel() {

    val ingresos: Flow<List<IngresoEntidad>> =
        repositorio.obtenerIngresos().map { lista -> lista.sortedByDescending { it.fecha } }

    val gastos: Flow<List<GastoEntidad>> =
        repositorio.obtenerGastos().map { lista -> lista.sortedByDescending { it.fecha } }

    // flujo de datos ya procesados
    val historialAgrupado: Flow<List<HistorialMes>> =
        repositorio.obtenerGastos().map { listaGastos ->

            // ordenar por fecha (mas recientes primero)
            val gastosOrdenados = listaGastos.sortedByDescending { it.fecha }
            val totalesPorMes = listaGastos
                .mapNotNull { gasto -> gasto.yearMonthOrNull()?.let { it to gasto } }
                .groupBy(keySelector = { it.first }, valueTransform = { it.second })
                .mapValues { (_, gastos) -> gastos.sumOf { it.cantidadCentimos } }
            val mesesOrdenados = totalesPorMes.keys.sorted()

            // agrupar por mes (yyyy-mm)
            val agrupados = gastosOrdenados
                .mapNotNull { gasto -> gasto.yearMonthOrNull()?.let { it to gasto } }
                .groupBy(keySelector = { it.first }, valueTransform = { it.second })

            // convertir a modelo para la UI
            agrupados.map { (mes, gastos) ->
                val totalMes = gastos.sumOf { it.cantidadCentimos }
                val indiceMes = mesesOrdenados.indexOf(mes)
                val mesAnterior = mesesOrdenados.getOrNull(indiceMes - 1)
                val totalMesAnterior = mesAnterior?.let { totalesPorMes[it] }
                val comparacion = if (totalMesAnterior != null && totalMesAnterior > 0L) {
                    ((totalMes - totalMesAnterior).toDouble() / totalMesAnterior.toDouble()) * 100.0
                } else {
                    null
                }

                HistorialMes(
                    mes = mes.toString(),
                    gastos = gastos,
                    totalGastadoCentimos = totalMes,
                    comparacionMesAnterior = comparacion,
                    totalInnecesarioCentimos = gastos
                        .filter { it.etiqueta == "Innecesario" }
                        .sumOf { it.cantidadCentimos }
                )
            }
        }

    val consejoAhorro: Flow<String> =
        historialAgrupado.map { historial ->
            val mesActual = historial.firstOrNull()
                ?: return@map "Registra tus primeros gastos para empezar a ver consejos de ahorro."

            val porcentajeInnecesario =
                if (mesActual.totalGastadoCentimos > 0L)
                    (mesActual.totalInnecesarioCentimos.toDouble() / mesActual.totalGastadoCentimos.toDouble()) * 100.0
                else 0.0

            val comparacion = mesActual.comparacionMesAnterior

            when {
                porcentajeInnecesario >= 30.0 ->
                    "Estas gastando mucho en cosas innecesarias (${formatear(porcentajeInnecesario)}% del mes). Intenta reducir esos gastos para ahorrar mas."

                comparacion != null && comparacion < 0.0 ->
                    "Felicidades! Estas gastando ${formatear(kotlin.math.abs(comparacion))}% menos que el mes anterior. Eso significa mayor ahorro."

                comparacion != null && comparacion > 0.0 ->
                    "Este mes gastaste ${formatear(comparacion)}% mas que el mes anterior. Revisa los gastos innecesarios y ajusta donde puedas."

                else ->
                    "Buen trabajo registrando tus gastos. Etiquetarlos te ayudara a detectar donde puedes ahorrar mas."
            }
        }

    fun actualizarIngreso(ingreso: IngresoEntidad) {
        viewModelScope.launch { repositorio.actualizarIngreso(ingreso) }
    }

    fun eliminarIngreso(ingresoId: Int) {
        viewModelScope.launch { repositorio.eliminarIngreso(ingresoId) }
    }

    fun actualizarGasto(gasto: GastoEntidad) {
        viewModelScope.launch { repositorio.actualizarGasto(gasto) }
    }

    fun eliminarGasto(gastoId: Int) {
        viewModelScope.launch { repositorio.eliminarGasto(gastoId) }
    }

    private fun formatear(valor: Double): String =
        String.format(Locale.US, "%.1f", valor)

    private fun GastoEntidad.yearMonthOrNull(): YearMonth? =
        try {
            YearMonth.from(FechaQuri.parsear(fecha))
        } catch (_: Exception) {
            null
        }
}

