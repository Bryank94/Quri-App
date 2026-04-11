package com.example.quritfg.ui.viewmodels

/**
 * Enum con las categorias de gasto.
 *
 * Se usa para mostrar opciones en la UI
 * y evitar usar strings sueltos.
 */
enum class CategoriaGasto(val texto: String) {

    // categorias disponibles
    COMIDA("Comida"),
    TRANSPORTE("Transporte"),
    OCIO("Ocio"),
    HOGAR("Hogar"),
    SALUD("Salud"),
    OTROS("Otros")
}