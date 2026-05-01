package com.example.quritfg.datos

import android.content.Context

/**
 * Se encarga de gestionar la sesion del usuario.
 *
 * Usa SharedPreferences para guardar si el usuario
 * esta logueado o no, de forma sencilla.
 *
 * No guarda datos complejos, solo estado de login.
 */
class SesionManager(context: Context) {

    // acceso a preferencias locales
    private val prefs = context.getSharedPreferences("quri_session", Context.MODE_PRIVATE)

    companion object {
        // clave para saber si hay sesion activa
        private const val KEY_LOGGED_IN = "logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
    }

    /**
     * Marca la sesion como activa.
     */
    fun guardarSesionActiva(usuarioId: Int, email: String) {
        prefs.edit()
            .putBoolean(KEY_LOGGED_IN, true)
            .putInt(KEY_USER_ID, usuarioId)
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }

    /**
     * Cierra la sesion del usuario.
     */
    fun cerrarSesion() {
        prefs.edit().clear().apply()
    }

    /**
     * Devuelve si el usuario esta logueado o no.
     */
    fun estaLogueado(): Boolean {
        return prefs.getBoolean(KEY_LOGGED_IN, false) && obtenerUsuarioId() != null
    }

    fun obtenerUsuarioId(): Int? {
        val usuarioId = prefs.getInt(KEY_USER_ID, -1)
        return usuarioId.takeIf { it > 0 }
    }

    fun obtenerEmailUsuario(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }
}
