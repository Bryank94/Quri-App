package com.example.quritfg.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.quritfg.datos.local.GastoEntidad
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom
import com.example.quritfg.ui.modelo.HistorialMes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    // flujo de datos ya procesados
    val historialAgrupado: Flow<List<HistorialMes>> =
        repositorio.obtenerGastos().map { listaGastos ->

            // ordenar por fecha (mas recientes primero)
            val gastosOrdenados = listaGastos.sortedByDescending { it.fecha }
            val totalesPorMes = listaGastos
                .groupBy { it.fecha.substring(0, 7) }
                .mapValues { (_, gastos) -> gastos.sumOf { it.cantidad } }
            val mesesOrdenados = totalesPorMes.keys.sorted()

            // agrupar por mes (yyyy-mm)
            val agrupados = gastosOrdenados.groupBy { gasto ->
                gasto.fecha.substring(0, 7)
            }

            // convertir a modelo para la UI
            agrupados.map { (mes, gastos) ->
                val totalMes = gastos.sumOf { it.cantidad }
                val indiceMes = mesesOrdenados.indexOf(mes)
                val mesAnterior = mesesOrdenados.getOrNull(indiceMes - 1)
                val totalMesAnterior = mesAnterior?.let { totalesPorMes[it] }
                val comparacion = if (totalMesAnterior != null && totalMesAnterior > 0.0) {
                    ((totalMes - totalMesAnterior) / totalMesAnterior) * 100.0
                } else {
                    null
                }

                HistorialMes(
                    mes = mes,
                    gastos = gastos,
                    totalGastado = totalMes,
                    comparacionMesAnterior = comparacion,
                    totalInnecesario = gastos
                        .filter { it.etiqueta == "Innecesario" }
                        .sumOf { it.cantidad }
                )
            }
        }

    val consejoAhorro: Flow<String> =
        historialAgrupado.map { historial ->
            val mesActual = historial.firstOrNull()
                ?: return@map "Registra tus primeros gastos para empezar a ver consejos de ahorro."

            val porcentajeInnecesario =
                if (mesActual.totalGastado > 0.0)
                    (mesActual.totalInnecesario / mesActual.totalGastado) * 100.0
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

    private fun formatear(valor: Double): String =
        String.format(Locale.US, "%.1f", valor)
}
