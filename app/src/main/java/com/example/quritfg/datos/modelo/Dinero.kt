package com.example.quritfg.datos.modelo

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

fun textoACentimos(texto: String): Long? {
    val normalizado = texto.trim().replace(',', '.')
    if (normalizado.isBlank()) return null

    return try {
        BigDecimal(normalizado)
            .setScale(2, RoundingMode.HALF_UP)
            .multiply(BigDecimal(100))
            .longValueExact()
    } catch (_: ArithmeticException) {
        null
    } catch (_: NumberFormatException) {
        null
    }
}

fun centimosAEuros(centimos: Long): BigDecimal =
    BigDecimal(centimos).divide(BigDecimal(100), 2, RoundingMode.UNNECESSARY)

fun formatearDinero(centimos: Long): String {
    val formato = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-ES"))
    return formato.format(centimosAEuros(centimos))
}
