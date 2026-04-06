package com.example.quritfg.datos.repositorio

import com.example.quritfg.datos.local.*
import kotlinx.coroutines.flow.Flow

class RepositorioQuriRoom(

    // DAOs
    private val metaDao: MetaDao,
    private val gastoDao: GastoDao,
    private val ingresoDao: IngresoDao,
    private val usuarioDao: UsuarioDao // 🔥 NUEVO

) {

    // -------------------------
    // META
    // -------------------------
    fun obtenerMeta(): Flow<MetaEntidad?> =
        metaDao.obtenerMeta()

    suspend fun guardarMeta(meta: MetaEntidad) =
        metaDao.insertarMeta(meta)

    // -------------------------
    // GASTOS
    // -------------------------
    fun obtenerGastos(): Flow<List<GastoEntidad>> =
        gastoDao.obtenerGastos()

    suspend fun anadirGasto(gasto: GastoEntidad) =
        gastoDao.insertarGasto(gasto)

    // -------------------------
    // INGRESOS
    // -------------------------
    fun obtenerIngresos(): Flow<List<IngresoEntidad>> =
        ingresoDao.obtenerIngresos()

    suspend fun anadirIngreso(ingreso: IngresoEntidad) =
        ingresoDao.insertarIngreso(ingreso)

    // -------------------------
    // USUARIOS 🔥 NUEVO
    // -------------------------
    suspend fun registrarUsuario(usuario: UsuarioEntidad) =
        usuarioDao.insertarUsuario(usuario)

    suspend fun login(email: String, password: String): UsuarioEntidad? =
        usuarioDao.login(email, password)

    suspend fun existeUsuario(email: String): UsuarioEntidad? =
        usuarioDao.obtenerUsuarioPorEmail(email)
}