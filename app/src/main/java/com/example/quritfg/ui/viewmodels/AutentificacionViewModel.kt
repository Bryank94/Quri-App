package com.example.quritfg.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quritfg.datos.local.UsuarioEntidad
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom
import kotlinx.coroutines.launch

class AutentificacionViewModel(
    private val repositorio: RepositorioQuriRoom
) : ViewModel() {

    fun registrarUsuario(
        email: String,
        password: String,
        onResultado: (Boolean) -> Unit
    ) {
        viewModelScope.launch {

            val existe = repositorio.existeUsuario(email)

            if (existe != null) {
                onResultado(false)
            } else {
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

    fun iniciarSesion(
        email: String,
        password: String,
        onResultado: (Boolean) -> Unit
    ) {
        viewModelScope.launch {

            val usuario = repositorio.login(email, password)

            onResultado(usuario != null)
        }
    }
}