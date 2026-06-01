package com.example.quritfg.datos.modelo

import com.example.quritfg.datos.local.GastoEntidad
import com.example.quritfg.datos.local.IngresoEntidad
import com.example.quritfg.datos.local.MetaEntidad
import com.example.quritfg.shared.modelo.FondoAhorro
import com.example.quritfg.shared.modelo.MovimientoFinanciero
import com.example.quritfg.shared.recompensas.RecompensasQuriComun
import java.time.LocalDate

object RecompensasQuri {
    const val PUNTOS_POR_LOGIN_DIARIO = RecompensasQuriComun.PUNTOS_POR_LOGIN_DIARIO
    const val BONUS_RACHA_SEMANAL = RecompensasQuriComun.BONUS_RACHA_SEMANAL
    const val PUNTOS_POR_EURO_AHORRADO = RecompensasQuriComun.PUNTOS_POR_EURO_AHORRADO
    const val BONUS_META_SEMANAL = RecompensasQuriComun.BONUS_META_SEMANAL
    const val BONUS_AMIGO_INVITADO = RecompensasQuriComun.BONUS_AMIGO_INVITADO
    const val PUNTOS_MINIMOS_CANJE = RecompensasQuriComun.PUNTOS_MINIMOS_CANJE
    const val CENTIMOS_POR_CANJE_MINIMO = RecompensasQuriComun.CENTIMOS_POR_CANJE_MINIMO
    const val TOPE_CANJE_MENSUAL_CENTIMOS = RecompensasQuriComun.TOPE_CANJE_MENSUAL_CENTIMOS

    fun calcularPuntos(
        ingresos: List<IngresoEntidad>,
        gastos: List<GastoEntidad>,
        fondos: List<MetaEntidad>,
        inicioDiarioRegistrado: Boolean = true,
        rachaSemanal: Boolean = false,
        perfilVerificado: Boolean = false,
        amigosInvitados: Int = 0,
        fechaActual: LocalDate = LocalDate.now()
    ): Int = RecompensasQuriComun.calcularPuntos(
        ingresos = ingresos.map { MovimientoFinanciero(it.cantidadCentimos, it.fecha, etiqueta = it.concepto.orEmpty()) },
        gastos = gastos.map { MovimientoFinanciero(it.cantidadCentimos, it.fecha, categoria = it.categoria, etiqueta = it.etiqueta) },
        fondos = fondos.map {
            FondoAhorro(
                nombre = it.nombre,
                cantidadObjetivoCentimos = it.cantidadObjetivoCentimos,
                cantidadActualCentimos = it.cantidadActualCentimos,
                fechaLimite = it.fechaLimite,
                prioridad = it.prioridad
            )
        },
        inicioDiarioRegistrado = inicioDiarioRegistrado,
        rachaSemanal = rachaSemanal,
        perfilVerificado = perfilVerificado,
        amigosInvitados = amigosInvitados,
        fechaActual = fechaActual.toString()
    )

    fun centimosCanjeables(puntos: Int): Long =
        RecompensasQuriComun.centimosCanjeables(puntos)

    fun puntosHastaCanje(puntos: Int): Int =
        RecompensasQuriComun.puntosHastaCanje(puntos)
}
