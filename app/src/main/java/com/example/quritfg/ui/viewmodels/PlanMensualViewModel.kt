package com.example.quritfg.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom

class PlanMensualViewModel(
    repositorio: RepositorioQuriRoom
) : ViewModel() {
    val fondos = repositorio.obtenerFondos()
    val ingresosDetectados = repositorio.obtenerIngresosDetectados()
}

class PlanMensualViewModelFactory(
    private val repositorio: RepositorioQuriRoom
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlanMensualViewModel::class.java)) {
            return PlanMensualViewModel(repositorio) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}
