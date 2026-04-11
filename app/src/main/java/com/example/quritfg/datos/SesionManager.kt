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
    }

    /**
     * Marca la sesion como activa.
     */
    fun guardarSesionActiva() {
        prefs.edit().putBoolean(KEY_LOGGED_IN, true).apply()
    }

    /**
     * Cierra la sesion del usuario.
     */
    fun cerrarSesion() {
        prefs.edit().putBoolean(KEY_LOGGED_IN, false).apply()
    }

    /**
     * Devuelve si el usuario esta logueado o no.
     */
    fun estaLogueado(): Boolean {
        return prefs.getBoolean(KEY_LOGGED_IN, false)
    }
}