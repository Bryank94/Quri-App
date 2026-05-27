package com.example.quritfg

import com.example.quritfg.datos.local.GastoEntidad
import com.example.quritfg.datos.local.IngresoEntidad
import com.example.quritfg.datos.local.MetaEntidad
import com.example.quritfg.datos.modelo.RecompensasQuri
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class RecompensasQuriTest {

    @Test
    fun calculaPuntosPorAhorroNetoYMetaCumplida() {
        val ingresos = listOf(IngresoEntidad(usuarioId = 1, cantidadCentimos = 250_00, fecha = "2027-01-10"))
        val gastos = listOf(GastoEntidad(usuarioId = 1, categoria = "Ocio", cantidadCentimos = 40_00, fecha = "2027-01-10", etiqueta = "Innecesario"))
        val fondos = listOf(
            MetaEntidad(
                usuarioId = 1,
                nombre = "Emergencia",
                cantidadObjetivoCentimos = 200_00,
                cantidadActualCentimos = 200_00,
                fechaLimite = "2027-12-31"
            )
        )

        val puntos = RecompensasQuri.calcularPuntos(
            ingresos = ingresos,
            gastos = gastos,
            fondos = fondos,
            inicioDiarioRegistrado = true,
            rachaSemanal = true,
            fechaActual = LocalDate.parse("2027-01-10")
        )

        assertEquals(340, puntos)
    }

    @Test
    fun canjeRespetaMinimoYTopeMensual() {
        assertEquals(0L, RecompensasQuri.centimosCanjeables(999))
        assertEquals(50L, RecompensasQuri.centimosCanjeables(1_000))
        assertEquals(500L, RecompensasQuri.centimosCanjeables(20_000))
    }
}
