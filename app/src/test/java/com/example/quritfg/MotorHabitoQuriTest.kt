package com.example.quritfg

import com.example.quritfg.datos.local.GastoEntidad
import com.example.quritfg.datos.local.IngresoEntidad
import com.example.quritfg.datos.local.MetaEntidad
import com.example.quritfg.datos.modelo.MotorHabitoQuri
import com.example.quritfg.datos.modelo.NivelHabitoQuri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class MotorHabitoQuriTest {
    private val hoy = LocalDate.of(2026, 5, 22)

    @Test
    fun avisaCuandoLosGastosSuperanLosIngresos() {
        val informe = MotorHabitoQuri.analizar(
            ingresos = listOf(ingreso(800_00)),
            gastos = listOf(gasto(900_00, etiqueta = "Necesario")),
            fondos = emptyList(),
            hoy = hoy
        )

        assertEquals(NivelHabitoQuri.RIESGO, informe.recomendaciones.first().nivel)
        assertTrue(informe.recomendaciones.first().titulo.contains("Gastos"))
    }

    @Test
    fun detectaFondoVencidoComoRiesgo() {
        val informe = MotorHabitoQuri.analizar(
            ingresos = listOf(ingreso(1_300_00)),
            gastos = emptyList(),
            fondos = listOf(fondo(nombre = "Viaje", objetivo = 1_000_00, actual = 300_00, fecha = "01-05-2026")),
            hoy = hoy
        )

        assertTrue(informe.recomendaciones.any { it.nivel == NivelHabitoQuri.RIESGO && it.titulo.contains("Viaje") })
    }

    @Test
    fun recomiendaRefuerzoMensualCuandoElFondoVaLento() {
        val informe = MotorHabitoQuri.analizar(
            ingresos = listOf(ingreso(1_300_00)),
            gastos = listOf(gasto(300_00)),
            fondos = listOf(fondo(nombre = "Sofa", objetivo = 900_00, actual = 50_00, fecha = "22-08-2026")),
            hoy = hoy
        )

        assertTrue(informe.recomendaciones.any { it.titulo.contains("Refuerza Sofa") })
    }

    @Test
    fun marcaFondoCompletadoComoPositivo() {
        val informe = MotorHabitoQuri.analizar(
            ingresos = listOf(ingreso(1_300_00)),
            gastos = listOf(gasto(300_00)),
            fondos = listOf(fondo(nombre = "Emergencia", objetivo = 500_00, actual = 500_00, fecha = "22-12-2026")),
            hoy = hoy
        )

        assertTrue(informe.recomendaciones.any { it.nivel == NivelHabitoQuri.POSITIVO && it.titulo.contains("Emergencia") })
    }

    @Test
    fun avisaSiElGastoInnecesarioEsAlto() {
        val informe = MotorHabitoQuri.analizar(
            ingresos = listOf(ingreso(1_300_00)),
            gastos = listOf(gasto(300_00, "Innecesario"), gasto(600_00, "Necesario")),
            fondos = listOf(fondo(nombre = "Viaje", objetivo = 1_000_00, actual = 500_00, fecha = "22-12-2026")),
            hoy = hoy
        )

        assertTrue(informe.recomendaciones.any { it.titulo.contains("Recorta gasto innecesario") })
    }


    @Test
    fun detectaSubidaDeGastoFrenteAlMesAnterior() {
        val informe = MotorHabitoQuri.analizar(
            ingresos = listOf(ingreso(1_500_00, fecha = "22-05-2026")),
            gastos = listOf(
                gasto(1_000_00, etiqueta = "Necesario", fecha = "22-05-2026"),
                gasto(600_00, etiqueta = "Necesario", fecha = "22-04-2026")
            ),
            fondos = listOf(fondo(nombre = "Viaje", objetivo = 1_000_00, actual = 600_00, fecha = "22-12-2026")),
            hoy = hoy
        )

        assertTrue(informe.insights.any { it.titulo.contains("Gasto mensual acelerado") })
    }

    @Test
    fun detectaCategoriaConMayorSubida() {
        val informe = MotorHabitoQuri.analizar(
            ingresos = listOf(ingreso(1_500_00, fecha = "22-05-2026")),
            gastos = listOf(
                gasto(250_00, etiqueta = "Innecesario", fecha = "22-05-2026", categoria = "Ocio"),
                gasto(50_00, etiqueta = "Innecesario", fecha = "22-04-2026", categoria = "Ocio")
            ),
            fondos = listOf(fondo(nombre = "Viaje", objetivo = 1_000_00, actual = 600_00, fecha = "22-12-2026")),
            hoy = hoy
        )

        assertTrue(informe.insights.any { it.titulo.contains("Ojo con Ocio") })
    }

    @Test
    fun calculaSaludFinancieraBajaSiNoHayLiquidezParaFondos() {
        val informe = MotorHabitoQuri.analizar(
            ingresos = listOf(ingreso(1_000_00, fecha = "22-05-2026")),
            gastos = listOf(gasto(900_00, etiqueta = "Necesario", fecha = "22-05-2026")),
            fondos = listOf(fondo(nombre = "Viaje", objetivo = 1_200_00, actual = 0, fecha = "22-08-2026")),
            hoy = hoy
        )

        assertTrue(informe.saludFinanciera < 60)
        assertTrue(informe.insights.any { it.titulo.contains("Fondos bajo presion") })
    }

    @Test
    fun prediceCuandoUnFondoNoLlegaATiempo() {
        val informe = MotorHabitoQuri.analizar(
            ingresos = listOf(ingreso(1_000_00, fecha = "22-05-2026")),
            gastos = listOf(gasto(900_00, etiqueta = "Necesario", fecha = "22-05-2026")),
            fondos = listOf(fondo(nombre = "Viaje", objetivo = 1_200_00, actual = 0, fecha = "22-08-2026")),
            hoy = hoy
        )

        assertTrue(informe.insights.any { it.titulo.contains("Viaje: no llegas a tiempo") })
    }

    @Test
    fun detectaGastoRepetidoEnTresMeses() {
        val informe = MotorHabitoQuri.analizar(
            ingresos = listOf(ingreso(1_500_00, fecha = "22-05-2026")),
            gastos = listOf(
                gasto(12_00, etiqueta = "Innecesario", fecha = "22-05-2026", categoria = "Streaming"),
                gasto(12_50, etiqueta = "Innecesario", fecha = "22-04-2026", categoria = "Streaming"),
                gasto(11_90, etiqueta = "Innecesario", fecha = "22-03-2026", categoria = "Streaming")
            ),
            fondos = listOf(fondo(nombre = "Emergencia", objetivo = 500_00, actual = 100_00, fecha = "22-12-2026")),
            hoy = hoy
        )

        assertTrue(informe.insights.any { it.titulo.contains("Gasto repetido: Streaming") })
    }

    @Test
    fun losInsightsExplicanPorQueRecomiendanAlgo() {
        val informe = MotorHabitoQuri.analizar(
            ingresos = listOf(ingreso(1_500_00, fecha = "22-05-2026")),
            gastos = listOf(
                gasto(1_000_00, etiqueta = "Necesario", fecha = "22-05-2026"),
                gasto(600_00, etiqueta = "Necesario", fecha = "22-04-2026")
            ),
            fondos = listOf(fondo(nombre = "Viaje", objetivo = 1_000_00, actual = 600_00, fecha = "22-12-2026")),
            hoy = hoy
        )

        assertTrue(informe.insights.any { !it.explicacion.isNullOrBlank() })
    }
    private fun ingreso(cantidad: Long, fecha: String = "22-05-2026") = IngresoEntidad(
        usuarioId = 1,
        cantidadCentimos = cantidad,
        fecha = fecha,
        concepto = "Nomina"
    )

    private fun gasto(
        cantidad: Long,
        etiqueta: String = "Necesario",
        fecha: String = "22-05-2026",
        categoria: String = "Ocio"
    ) = GastoEntidad(
        usuarioId = 1,
        categoria = categoria,
        cantidadCentimos = cantidad,
        fecha = fecha,
        etiqueta = etiqueta
    )

    private fun fondo(
        nombre: String,
        objetivo: Long,
        actual: Long,
        fecha: String
    ) = MetaEntidad(
        id = nombre.hashCode(),
        usuarioId = 1,
        nombre = nombre,
        cantidadObjetivoCentimos = objetivo,
        cantidadActualCentimos = actual,
        fechaLimite = fecha,
        prioridad = 2
    )
}

