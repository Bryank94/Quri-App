package com.example.quritfg.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.quritfg.datos.local.*
import com.example.quritfg.datos.modelo.ResumenFinanciero
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * ViewModel de la pantalla progreso.
 *
 * Hace los mismos calculos que inicio,
 * pero pensado para mostrar un analisis mas detallado.
 */
class ProgresoViewModel(
    private val repositorio: RepositorioQuriRoom
) : ViewModel() {

    // datos base
    val metaActual: Flow<MetaEntidad?> =
        repositorio.obtenerMeta()

    val listaGastos: Flow<List<GastoEntidad>> =
        repositorio.obtenerGastos()

    val listaIngresos: Flow<List<IngresoEntidad>> =
        repositorio.obtenerIngresos()

    /**
     * Calcula el resumen financiero
     */
    val resumenFinanciero: Flow<ResumenFinanciero> =
        combine(metaActual, listaGastos, listaIngresos) { meta, gastos, ingresos ->

            // calculos principales
            val totalGastos = gastos.sumOf { it.cantidad }
            val totalIngresos = ingresos.sumOf { it.cantidad }
            val ahorroActual = totalIngresos - totalGastos
            val objetivo = meta?.cantidadObjetivo ?: 0.0

            // progreso (limitado entre 0 y 1)
            val porcentajeProgreso =
                if (objetivo > 0.0)
                    (ahorroActual / objetivo).toFloat().coerceIn(0f, 1f)
                else 0f

            // lo que falta por ahorrar
            val ahorroRestante =
                if (objetivo > 0.0) objetivo - ahorroActual else 0.0

            // resultado final
            ResumenFinanciero(
                totalIngresos = totalIngresos,
                totalGastos = totalGastos,
                ahorroActual = ahorroActual,
                objetivo = objetivo,
                porcentajeProgreso = porcentajeProgreso,
                ahorroRestante = ahorroRestante
            )
        }
}