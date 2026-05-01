package com.example.quritfg.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.quritfg.datos.local.GastoEntidad
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom
import com.example.quritfg.ui.modelo.HistorialMes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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

            // agrupar por mes (yyyy-mm)
            val agrupados = gastosOrdenados.groupBy { gasto ->
                gasto.fecha.substring(0, 7)
            }

            // convertir a modelo para la UI
            agrupados.map { (mes, gastos) ->
                HistorialMes(
                    mes = mes,
                    gastos = gastos
                )
            }
        }
}