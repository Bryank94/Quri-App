package com.example.quritfg.datos

import android.content.Context
import com.google.firebase.auth.FirebaseAuth

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
        private const val KEY_FIREBASE_UID = "firebase_uid"
        private const val KEY_ONBOARDING_VISTO = "onboarding_visto"
    }

    /**
     * Marca la sesion como activa.
     */
    fun guardarSesionActiva(usuarioId: Int, email: String, firebaseUid: String? = null) {
        prefs.edit()
            .putBoolean(KEY_LOGGED_IN, true)
            .putInt(KEY_USER_ID, usuarioId)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_FIREBASE_UID, firebaseUid)
            .apply()
    }

    /**
     * Cierra la sesion del usuario.
     */
    fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        prefs.edit().clear().apply()
    }

    /**
     * Devuelve si el usuario esta logueado o no.
     */
    fun estaLogueado(): Boolean {
        val sesionLocalValida = prefs.getBoolean(KEY_LOGGED_IN, false) && obtenerUsuarioId() != null
        if (!sesionLocalValida) return false

        val firebaseUid = prefs.getString(KEY_FIREBASE_UID, null)
        return firebaseUid == null || FirebaseAuth.getInstance().currentUser?.uid == firebaseUid
    }

    fun obtenerUsuarioId(): Int? {
        val usuarioId = prefs.getInt(KEY_USER_ID, -1)
        return usuarioId.takeIf { it > 0 }
    }

    fun obtenerEmailUsuario(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    fun onboardingVisto(): Boolean =
        prefs.getBoolean(KEY_ONBOARDING_VISTO, false)

    fun marcarOnboardingVisto() {
        prefs.edit().putBoolean(KEY_ONBOARDING_VISTO, true).apply()
    }
}
