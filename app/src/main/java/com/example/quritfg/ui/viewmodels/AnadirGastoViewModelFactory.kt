package com.example.quritfg.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom

/**
 * Clase encargada de crear el ViewModel AnadirMovimientoViewModel.
 * Permite pasar el repositorio al constructor del ViewModel.
 */
class AnadirMovimientoViewModelFactory(
    private val repositorio: RepositorioQuriRoom
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnadirMovimientoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnadirMovimientoViewModel(repositorio) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}