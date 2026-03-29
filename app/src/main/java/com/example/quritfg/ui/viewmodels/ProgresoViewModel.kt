package com.example.quritfg.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.quritfg.datos.local.*
import com.example.quritfg.datos.modelo.ResumenFinanciero
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ProgresoViewModel(
    private val repositorio: RepositorioQuriRoom
) : ViewModel() {

    val metaActual: Flow<MetaEntidad?> =
        repositorio.obtenerMeta()

    val listaGastos: Flow<List<GastoEntidad>> =
        repositorio.obtenerGastos()

    val listaIngresos: Flow<List<IngresoEntidad>> =
        repositorio.obtenerIngresos()

    val resumenFinanciero: Flow<ResumenFinanciero> =
        combine(metaActual, listaGastos, listaIngresos) { meta, gastos, ingresos ->

            val totalGastos = gastos.sumOf { it.cantidad }
            val totalIngresos = ingresos.sumOf { it.cantidad }
            val ahorroActual = totalIngresos - totalGastos
            val objetivo = meta?.cantidadObjetivo ?: 0.0

            val porcentajeProgreso =
                if (objetivo > 0.0)
                    (ahorroActual / objetivo).toFloat().coerceIn(0f, 1f)
                else 0f

            val ahorroRestante =
                if (objetivo > 0.0) objetivo - ahorroActual else 0.0

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