package com.example.quritfg.datos.repositorio

import android.database.sqlite.SQLiteConstraintException
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
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class RepositorioQuriRoom(
    private val metaDao: MetaDao,
    private val gastoDao: GastoDao,
    private val ingresoDao: IngresoDao,
    private val usuarioDao: UsuarioDao,
    private val sesionManager: SesionManager
) {

    private companion object {
        const val AUTH_VERSION_PBKDF2 = 2
        const val PBKDF2_ITERATIONS = 120_000
        const val PASSWORD_KEY_LENGTH_BITS = 256
        const val SALT_BYTES = 32
    }

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
        return try {
            usuarioDao.insertarUsuario(
                UsuarioEntidad(
                    email = emailNormalizado,
                    passwordHash = crearHashSeguro(password, salt, PBKDF2_ITERATIONS),
                    passwordSalt = salt,
                    authVersion = AUTH_VERSION_PBKDF2,
                    passwordIterations = PBKDF2_ITERATIONS
                )
            )

            usuarioDao.obtenerUsuarioPorEmail(emailNormalizado)
        } catch (_: SQLiteConstraintException) {
            null
        }
    }

    suspend fun login(email: String, password: String): UsuarioEntidad? {
        val usuario = usuarioDao.obtenerUsuarioPorEmail(email.trim().lowercase()) ?: return null
        val loginValido = when (usuario.authVersion) {
            AUTH_VERSION_PBKDF2 -> verificarHashSeguro(password, usuario)
            else -> verificarHashAntiguo(password, usuario)
        }

        if (!loginValido) return null

        if (usuario.authVersion != AUTH_VERSION_PBKDF2) {
            actualizarPasswordASegura(usuario, password)
        }

        return usuario
    }

    suspend fun existeUsuario(email: String): UsuarioEntidad? =
        usuarioDao.obtenerUsuarioPorEmail(email.trim().lowercase())

    suspend fun obtenerOCrearUsuarioExterno(email: String): UsuarioEntidad? {
        val emailNormalizado = email.trim().lowercase()
        usuarioDao.obtenerUsuarioPorEmail(emailNormalizado)?.let { return it }

        val salt = generarSalt()
        return try {
            usuarioDao.insertarUsuario(
                UsuarioEntidad(
                    email = emailNormalizado,
                    passwordHash = crearHashSeguro(generarSalt(), salt, PBKDF2_ITERATIONS),
                    passwordSalt = salt,
                    authVersion = AUTH_VERSION_PBKDF2,
                    passwordIterations = PBKDF2_ITERATIONS
                )
            )

            usuarioDao.obtenerUsuarioPorEmail(emailNormalizado)
        } catch (_: SQLiteConstraintException) {
            usuarioDao.obtenerUsuarioPorEmail(emailNormalizado)
        }
    }

    private fun usuarioActivoId(): Int? =
        sesionManager.obtenerUsuarioId()

    private fun generarSalt(): String {
        val bytes = ByteArray(SALT_BYTES)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun crearHashSeguro(password: String, salt: String, iterations: Int): String {
        val saltBytes = Base64.decode(salt, Base64.NO_WRAP)
        val spec = PBEKeySpec(password.toCharArray(), saltBytes, iterations, PASSWORD_KEY_LENGTH_BITS)
        val key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec)
        return Base64.encodeToString(key.encoded, Base64.NO_WRAP)
    }

    private suspend fun actualizarPasswordASegura(usuario: UsuarioEntidad, password: String) {
        val salt = generarSalt()
        usuarioDao.actualizarCredenciales(
            usuarioId = usuario.id,
            passwordHash = crearHashSeguro(password, salt, PBKDF2_ITERATIONS),
            passwordSalt = salt,
            authVersion = AUTH_VERSION_PBKDF2,
            passwordIterations = PBKDF2_ITERATIONS
        )
    }

    private fun verificarHashSeguro(password: String, usuario: UsuarioEntidad): Boolean {
        val hashEntrada = crearHashSeguro(password, usuario.passwordSalt, usuario.passwordIterations)
        return comparacionConstante(hashEntrada, usuario.passwordHash)
    }

    private fun verificarHashAntiguo(password: String, usuario: UsuarioEntidad): Boolean {
        val hashEntrada = crearHashAntiguo(password, usuario.passwordSalt)
        return comparacionConstante(hashEntrada, usuario.passwordHash)
    }

    private fun crearHashAntiguo(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest("$salt:$password".toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun comparacionConstante(a: String, b: String): Boolean =
        MessageDigest.isEqual(
            a.toByteArray(Charsets.UTF_8),
            b.toByteArray(Charsets.UTF_8)
        )
}
