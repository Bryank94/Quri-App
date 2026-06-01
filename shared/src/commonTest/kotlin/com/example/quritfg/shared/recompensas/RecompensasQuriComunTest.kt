package com.example.quritfg.shared.recompensas

import com.example.quritfg.shared.modelo.FondoAhorro
import com.example.quritfg.shared.modelo.MovimientoFinanciero
import kotlin.test.Test
import kotlin.test.assertEquals

class RecompensasQuriComunTest {
    @Test
    fun calculaPuntosCompartidosPorAhorroYMetas() {
        val puntos = RecompensasQuriComun.calcularPuntos(
            ingresos = listOf(MovimientoFinanciero(cantidadCentimos = 1_000_00, fecha = "30-05-2026")),
            gastos = listOf(MovimientoFinanciero(cantidadCentimos = 250_00, fecha = "30-05-2026")),
            fondos = listOf(
                FondoAhorro(
                    nombre = "Emergencia",
                    cantidadObjetivoCentimos = 100_00,
                    cantidadActualCentimos = 100_00,
                    fechaLimite = "31-12-2026"
                )
            ),
            rachaSemanal = true,
            perfilVerificado = true,
            amigosInvitados = 1,
            fechaActual = "2026-05-30"
        )

        assertEquals(1_330, puntos)
    }

    @Test
    fun calculaCanjeYDistanciaAlSiguienteCanje() {
        assertEquals(0L, RecompensasQuriComun.centimosCanjeables(999))
        assertEquals(50L, RecompensasQuriComun.centimosCanjeables(1_000))
        assertEquals(500L, RecompensasQuriComun.centimosCanjeables(20_000))
        assertEquals(1, RecompensasQuriComun.puntosHastaCanje(999))
    }
}
