package com.example.quritfg.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * ViewModel que gestiona la validación del formulario de registro.
 */
class RegistroViewModel : ViewModel() {

    // Estados del formulario
    var correo by mutableStateOf("")
        private set

    var contrasena by mutableStateOf("")
        private set

    // Estados de error
    var errorCorreo by mutableStateOf<String?>(null)
        private set

    var errorContrasena by mutableStateOf<String?>(null)
        private set

    // Indica si el registro es válido
    var registroValido by mutableStateOf(false)
        private set

    // Se ejecuta cuando cambia el correo
    fun onCorreoCambiado(nuevoCorreo: String) {
        correo = nuevoCorreo
        validar()
    }

    // Se ejecuta cuando cambia la contraseña
    fun onContrasenaCambiada(nuevaContrasena: String) {
        contrasena = nuevaContrasena
        validar()
    }

    /**
     * Valida los datos introducidos.
     * Controla formato del correo y longitud mínima de la contraseña.
     */
    private fun validar() {
        errorCorreo = when {
            correo.isBlank() -> "El correo es obligatorio"
            !correo.contains("@") -> "El correo no es válido"
            else -> null
        }

        errorContrasena = when {
            contrasena.isBlank() -> "La contraseña es obligatoria"
            contrasena.length < 6 -> "Debe tener al menos 6 caracteres"
            else -> null
        }

        registroValido = errorCorreo == null && errorContrasena == null
    }
}