package com.example.quritfg.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.quritfg.datos.local.GastoEntidad
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom
import com.example.quritfg.ui.modelo.HistorialMes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HistorialViewModel(
    private val repositorio: RepositorioQuriRoom
) : ViewModel() {

    val historialAgrupado: Flow<List<HistorialMes>> =
        repositorio.obtenerGastos().map { listaGastos ->

            // 1️⃣ ordenar todos los gastos por fecha descendente
            val gastosOrdenados = listaGastos.sortedByDescending { it.fecha }

            // 2️⃣ agrupar por mes
            val agrupados = gastosOrdenados.groupBy { gasto ->
                gasto.fecha.substring(0, 7)
            }

            // 3️⃣ convertir cada grupo en HistorialMes
            agrupados.map { (mes, gastos) ->
                HistorialMes(
                    mes = mes,
                    gastos = gastos
                )
            }
        }
}