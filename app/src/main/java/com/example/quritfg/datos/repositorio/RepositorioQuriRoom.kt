package com.example.quritfg.datos.repositorio

import android.util.Base64
import com.example.quritfg.datos.SesionManager
import com.example.quritfg.datos.local.GastoDao
import com.example.quritfg.datos.local.GastoEntidad
import com.example.quritfg.datos.local.IngresoDao
import com.example.quritfg.datos.local.IngresoEntidad
import com.example.quritfg.datos.local.MetaDao
import com.example.quritfg.datos.local.MetaEntidad
import com.example.quritfg.datos.local.UsuarioDao
import com.example.quritfg.datos.local.UsuarioEntidad
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.security.MessageDigest
import java.security.SecureRandom

class RepositorioQuriRoom(
    private val metaDao: MetaDao,
    private val gastoDao: GastoDao,
    private val ingresoDao: IngresoDao,
    private val usuarioDao: UsuarioDao,
    private val sesionManager: SesionManager
) {

    fun obtenerMeta(): Flow<MetaEntidad?> {
        val usuarioId = usuarioActivoId() ?: return flowOf(null)
        return metaDao.obtenerMeta(usuarioId)
    }

    suspend fun guardarMeta(meta: MetaEntidad) {
        val usuarioId = usuarioActivoId() ?: return
        metaDao.insertarMeta(meta.copy(usuarioId = usuarioId))
    }

    fun obtenerGastos(): Flow<List<GastoEntidad>> {
        val usuarioId = usuarioActivoId() ?: return flowOf(emptyList())
        return gastoDao.obtenerGastos(usuarioId)
    }

    suspend fun anadirGasto(gasto: GastoEntidad) {
        val usuarioId = usuarioActivoId() ?: return
        gastoDao.insertarGasto(gasto.copy(usuarioId = usuarioId))
    }

    fun obtenerIngresos(): Flow<List<IngresoEntidad>> {
        val usuarioId = usuarioActivoId() ?: return flowOf(emptyList())
        return ingresoDao.obtenerIngresos(usuarioId)
    }

    suspend fun anadirIngreso(ingreso: IngresoEntidad) {
        val usuarioId = usuarioActivoId() ?: return
        ingresoDao.insertarIngreso(ingreso.copy(usuarioId = usuarioId))
    }

    suspend fun reiniciarDatosUsuario() {
        val usuarioId = usuarioActivoId() ?: return
        gastoDao.borrarTodos(usuarioId)
        ingresoDao.borrarTodos(usuarioId)
        metaDao.borrarTodas(usuarioId)
    }

    suspend fun registrarUsuario(email: String, password: String): UsuarioEntidad? {
        val emailNormalizado = email.trim().lowercase()

        if (usuarioDao.obtenerUsuarioPorEmail(emailNormalizado) != null) {
            return null
        }

        val salt = generarSalt()
        usuarioDao.insertarUsuario(
            UsuarioEntidad(
                email = emailNormalizado,
                passwordHash = crearHash(password, salt),
                passwordSalt = salt
            )
        )

        return usuarioDao.obtenerUsuarioPorEmail(emailNormalizado)
    }

    suspend fun login(email: String, password: String): UsuarioEntidad? {
        val usuario = usuarioDao.obtenerUsuarioPorEmail(email.trim().lowercase()) ?: return null
        val hashEntrada = crearHash(password, usuario.passwordSalt)
        return usuario.takeIf { hashEntrada == it.passwordHash }
    }

    suspend fun existeUsuario(email: String): UsuarioEntidad? =
        usuarioDao.obtenerUsuarioPorEmail(email.trim().lowercase())

    private fun usuarioActivoId(): Int? =
        sesionManager.obtenerUsuarioId()

    private fun generarSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun crearHash(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest("$salt:$password".toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}
