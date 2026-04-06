package com.example.quritfg.datos

import android.content.Context

class SesionManager(context: Context) {

    private val prefs = context.getSharedPreferences("quri_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LOGGED_IN = "logged_in"
    }

    fun guardarSesionActiva() {
        prefs.edit().putBoolean(KEY_LOGGED_IN, true).apply()
    }

    fun cerrarSesion() {
        prefs.edit().putBoolean(KEY_LOGGED_IN, false).apply()
    }

    fun estaLogueado(): Boolean {
        return prefs.getBoolean(KEY_LOGGED_IN, false)
    }
}