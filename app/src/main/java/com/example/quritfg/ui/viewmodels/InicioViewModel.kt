package com.example.quritfg.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.quritfg.datos.local.*
import com.example.quritfg.datos.modelo.ResumenFinanciero
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * ViewModel de la pantalla inicio.
 *
 * Aqui se combinan los datos de gastos, ingresos y fondos
 * para generar el resumen financiero.
 */
class InicioViewModel(
    private val repositorio: RepositorioQuriRoom
) : ViewModel() {

    // datos base
    val metaActual: Flow<MetaEntidad?> =
        repositorio.obtenerMeta()

    val fondos: Flow<List<MetaEntidad>> =
        repositorio.obtenerFondos()

    val listaGastos: Flow<List<GastoEntidad>> =
        repositorio.obtenerGastos()

    val listaIngresos: Flow<List<IngresoEntidad>> =
        repositorio.obtenerIngresos()

    /**
     * Flujo con todos los calculos ya hechos
     */
    val resumenFinanciero: Flow<ResumenFinanciero> =
        combine(fondos, listaGastos, listaIngresos) { fondos, gastos, ingresos ->

            // calculos basicos
            val totalGastos = gastos.sumOf { it.cantidadCentimos }
            val totalIngresos = ingresos.sumOf { it.cantidadCentimos }
            val ahorroActual = fondos.sumOf { it.cantidadActualCentimos }
            val objetivo = fondos.sumOf { it.cantidadObjetivoCentimos }

            // porcentaje de progreso (limitado entre 0 y 1)
            val porcentajeProgreso =
                if (objetivo > 0L)
                    (ahorroActual.toDouble() / objetivo.toDouble()).toFloat().coerceIn(0f, 1f)
                else 0f

            // dinero que falta
            val ahorroRestante =
                if (objetivo > 0L) objetivo - ahorroActual else 0L

            // resultado final para la UI
            ResumenFinanciero(
                totalIngresosCentimos = totalIngresos,
                totalGastosCentimos = totalGastos,
                ahorroActualCentimos = ahorroActual,
                objetivoCentimos = objetivo,
                porcentajeProgreso = porcentajeProgreso,
                ahorroRestanteCentimos = ahorroRestante
            )
        }
}
