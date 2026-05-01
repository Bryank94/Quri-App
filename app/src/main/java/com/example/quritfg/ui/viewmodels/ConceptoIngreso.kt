package com.example.quritfg.ui.viewmodels

/**
 * Enum con los conceptos de ingreso.
 *
 * Se usa para mostrar opciones en la UI
 * y evitar usar strings sueltos.
 */
enum class ConceptoIngreso(val texto: String) {

    // conceptos disponibles
    SALARIO("Salario"),
    PAGOS("Pagos"),
    OTROS("Otros")
}
