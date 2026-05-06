package com.example.quritfg.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.quritfg.datos.local.MetaEntidad
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom
import kotlinx.coroutines.flow.Flow

/**
 * ViewModel proporciona la meta actual almacenada.
 */
class MetasViewModel(
    private val repositorio: RepositorioQuriRoom
) : ViewModel() {

    // contiene la meta guardada en la base de datos
    val metaActual: Flow<MetaEntidad?> =
        repositorio.obtenerMeta()
}