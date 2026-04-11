package com.example.quritfg.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quritfg.datos.local.UsuarioEntidad
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de la autenticacion.
 *
 * Aqui se gestiona el registro y el login.
 * Toda la logica va contra el repositorio.
 */
class AutentificacionViewModel(
    private val repositorio: RepositorioQuriRoom
) : ViewModel() {

    /**
     * Registro de usuario
     *
     * Comprueba si ya existe antes de guardar
     */
    fun registrarUsuario(
        email: String,
        password: String,
        onResultado: (Boolean) -> Unit
    ) {
        viewModelScope.launch {

            val existe = repositorio.existeUsuario(email)

            if (existe != null) {
                // ya existe ese email
                onResultado(false)
            } else {
                // crea nuevo usuario
                repositorio.registrarUsuario(
                    UsuarioEntidad(
                        email = email,
                        password = password
                    )
                )
                onResultado(true)
            }
        }
    }

    /**
     * Login de usuario
     *
     * Devuelve true si encuentra coincidencia
     */
    fun iniciarSesion(
        email: String,
        password: String,
        onResultado: (Boolean) -> Unit
    ) {
        viewModelScope.launch {

            val usuario = repositorio.login(email, password)

            // si existe devuelve true
            onResultado(usuario != null)
        }
    }
}