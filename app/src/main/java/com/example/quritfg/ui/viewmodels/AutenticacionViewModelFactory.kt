package com.example.quritfg.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom

class AutenticacionViewModelFactory(
    private val repositorio: RepositorioQuriRoom
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AutentificacionViewModel(repositorio) as T
    }
}