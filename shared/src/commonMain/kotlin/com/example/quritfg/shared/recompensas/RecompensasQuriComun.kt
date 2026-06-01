package com.example.quritfg.shared.recompensas

import com.example.quritfg.shared.modelo.FechaQuriComunParser
import com.example.quritfg.shared.modelo.FondoAhorro
import com.example.quritfg.shared.modelo.MovimientoFinanciero

object RecompensasQuriComun {
    const val PUNTOS_POR_LOGIN_DIARIO = 5
    const val BONUS_RACHA_SEMANAL = 25
    const val PUNTOS_POR_EURO_AHORRADO = 1
    const val BONUS_META_SEMANAL = 100
    const val BONUS_AMIGO_INVITADO = 250
    const val PUNTOS_MINIMOS_CANJE = 1_000
    const val CENTIMOS_POR_CANJE_MINIMO = 50L
    const val TOPE_CANJE_MENSUAL_CENTIMOS = 500L

    fun calcularPuntos(
        ingresos: List<MovimientoFinanciero>,
        gastos: List<MovimientoFinanciero>,
        fondos: List<FondoAhorro>,
        inicioDiarioRegistrado: Boolean = true,
        rachaSemanal: Boolean = false,
        perfilVerificado: Boolean = false,
        amigosInvitados: Int = 0,
        fechaActual: String? = null
    ): Int {
        val ahorroNetoCentimos = (
            ingresos.sumOf { it.cantidadCentimos } - gastos.sumOf { it.cantidadCentimos }
        ).coerceAtLeast(0L)
        val puntosAhorro = (ahorroNetoCentimos / 100L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        val metasCumplidas = fondos.count {
            it.cantidadObjetivoCentimos > 0 && it.cantidadActualCentimos >= it.cantidadObjetivoCentimos
        }
        val metasConFechaVigente = fondos.count { fondo ->
            fechaActual == null || fechaVigente(fondo.fechaLimite, fechaActual)
        }
        val bonusLogin = if (inicioDiarioRegistrado) PUNTOS_POR_LOGIN_DIARIO else 0
        val bonusRacha = if (rachaSemanal) BONUS_RACHA_SEMANAL else 0
        val bonusPerfil = if (perfilVerificado) 200 else 0
        val bonusInvitaciones = amigosInvitados.coerceAtLeast(0) * BONUS_AMIGO_INVITADO
        val bonusMetas = metasCumplidas.coerceAtMost(metasConFechaVigente) * BONUS_META_SEMANAL

        return puntosAhorro + bonusLogin + bonusRacha + bonusPerfil + bonusInvitaciones + bonusMetas
    }

    fun centimosCanjeables(puntos: Int): Long {
        if (puntos < PUNTOS_MINIMOS_CANJE) return 0L
        val bloques = puntos / PUNTOS_MINIMOS_CANJE
        return (bloques * CENTIMOS_POR_CANJE_MINIMO).coerceAtMost(TOPE_CANJE_MENSUAL_CENTIMOS)
    }

    fun puntosHastaCanje(puntos: Int): Int =
        (PUNTOS_MINIMOS_CANJE - puntos).coerceAtLeast(0)

    private fun fechaVigente(fechaLimite: String, fechaActual: String): Boolean {
        val limite = FechaQuriComunParser.parsear(fechaLimite) ?: return false
        val actual = FechaQuriComunParser.parsear(fechaActual) ?: return false
        return limite >= actual
    }
}
