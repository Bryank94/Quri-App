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
        onResultado: (UsuarioEntidad?) -> Unit
    ) {
        viewModelScope.launch {
            onResultado(repositorio.registrarUsuario(email, password))
        }
    }

    fun iniciarSesion(
        email: String,
        password: String,
        onResultado: (UsuarioEntidad?) -> Unit
    ) {
        viewModelScope.launch {
            onResultado(repositorio.login(email, password))
        }
    }

    fun iniciarSesionExterna(
        email: String,
        onResultado: (UsuarioEntidad?) -> Unit
    ) {
        viewModelScope.launch {
            onResultado(repositorio.obtenerOCrearUsuarioExterno(email))
        }
    }
}
