package com.example.quritfg.datos.repositorio

import android.database.sqlite.SQLiteConstraintException
import android.util.Base64
import com.example.quritfg.datos.SesionManager
import com.example.quritfg.datos.local.GastoDao
import com.example.quritfg.datos.local.GastoEntidad
import com.example.quritfg.datos.local.IngresoDetectadoDao
import com.example.quritfg.datos.local.IngresoDetectadoEntidad
import com.example.quritfg.datos.local.IngresoDao
import com.example.quritfg.datos.local.IngresoEntidad
import com.example.quritfg.datos.local.MetaDao
import com.example.quritfg.datos.local.MetaEntidad
import com.example.quritfg.datos.local.RepartoDetectadoDao
import com.example.quritfg.datos.local.RepartoDetectadoEntidad
import com.example.quritfg.datos.local.UsuarioDao
import com.example.quritfg.datos.local.UsuarioEntidad
import com.example.quritfg.datos.modelo.FechaQuri
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
    private val ingresoDetectadoDao: IngresoDetectadoDao,
    private val repartoDetectadoDao: RepartoDetectadoDao,
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

    fun obtenerFondos(): Flow<List<MetaEntidad>> {
        val usuarioId = usuarioActivoId() ?: return flowOf(emptyList())
        return metaDao.obtenerFondos(usuarioId)
    }

    suspend fun guardarMeta(meta: MetaEntidad) {
        val usuarioId = usuarioActivoId() ?: return
        metaDao.insertarMeta(meta.copy(usuarioId = usuarioId))
    }

    suspend fun eliminarFondo(fondoId: Int) {
        val usuarioId = usuarioActivoId() ?: return
        metaDao.eliminarFondo(fondoId, usuarioId)
    }

    suspend fun aportarAFondo(fondoId: Int?, cantidadCentimos: Long) {
        val usuarioId = usuarioActivoId() ?: return
        if (fondoId == null || cantidadCentimos <= 0L) return
        metaDao.aportarAFondo(fondoId, usuarioId, cantidadCentimos)
    }

    fun obtenerGastos(): Flow<List<GastoEntidad>> {
        val usuarioId = usuarioActivoId() ?: return flowOf(emptyList())
        return gastoDao.obtenerGastos(usuarioId)
    }

    suspend fun anadirGasto(gasto: GastoEntidad) {
        val usuarioId = usuarioActivoId() ?: return
        gastoDao.insertarGasto(gasto.copy(usuarioId = usuarioId))
    }

    suspend fun actualizarGasto(gasto: GastoEntidad) {
        val usuarioId = usuarioActivoId() ?: return
        gastoDao.actualizarGasto(
            gastoId = gasto.id,
            usuarioId = usuarioId,
            categoria = gasto.categoria,
            cantidadCentimos = gasto.cantidadCentimos,
            fecha = gasto.fecha,
            etiqueta = gasto.etiqueta
        )
    }

    suspend fun eliminarGasto(gastoId: Int) {
        val usuarioId = usuarioActivoId() ?: return
        gastoDao.eliminarGasto(gastoId, usuarioId)
    }

    fun obtenerIngresos(): Flow<List<IngresoEntidad>> {
        val usuarioId = usuarioActivoId() ?: return flowOf(emptyList())
        return ingresoDao.obtenerIngresos(usuarioId)
    }

    fun obtenerUsuariosAdmin(): Flow<List<UsuarioEntidad>> =
        usuarioDao.obtenerTodosUsuarios()

    fun contarGastosAdmin(): Flow<Int> =
        gastoDao.contarTodos()

    fun contarIngresosAdmin(): Flow<Int> =
        ingresoDao.contarTodos()

    fun contarFondosAdmin(): Flow<Int> =
        metaDao.contarTodas()

    suspend fun anadirIngreso(ingreso: IngresoEntidad) {
        val usuarioId = usuarioActivoId() ?: return
        ingresoDao.insertarIngreso(ingreso.copy(usuarioId = usuarioId))
    }

    suspend fun actualizarIngreso(ingreso: IngresoEntidad) {
        val usuarioId = usuarioActivoId() ?: return
        ingresoDao.actualizarIngreso(
            ingresoId = ingreso.id,
            usuarioId = usuarioId,
            cantidadCentimos = ingreso.cantidadCentimos,
            fecha = ingreso.fecha,
            concepto = ingreso.concepto
        )
    }

    suspend fun eliminarIngreso(ingresoId: Int) {
        val usuarioId = usuarioActivoId() ?: return
        ingresoDao.eliminarIngreso(ingresoId, usuarioId)
    }

    fun obtenerIngresosDetectados(): Flow<List<IngresoDetectadoEntidad>> {
        val usuarioId = usuarioActivoId() ?: return flowOf(emptyList())
        return ingresoDetectadoDao.obtenerIngresosDetectados(usuarioId)
    }

    suspend fun guardarIngresoDetectado(ingreso: IngresoDetectadoEntidad) {
        val usuarioId = usuarioActivoId() ?: return
        ingresoDetectadoDao.insertarIngresoDetectado(ingreso.copy(usuarioId = usuarioId))
    }

    suspend fun confirmarIngresoDetectado(
        ingreso: IngresoEntidad,
        ingresoDetectado: IngresoDetectadoEntidad,
        repartos: List<RepartoDetectadoEntidad>
    ) {
        val usuarioId = usuarioActivoId() ?: return
        ingresoDao.insertarIngreso(ingreso.copy(usuarioId = usuarioId))
        repartos.forEach { reparto ->
            if (reparto.cantidadCentimos > 0L) {
                metaDao.aportarAFondo(reparto.fondoId, usuarioId, reparto.cantidadCentimos)
            }
        }
        val ingresoId = ingresoDetectadoDao.insertarIngresoDetectado(ingresoDetectado.copy(usuarioId = usuarioId)).toInt()
        repartoDetectadoDao.insertarRepartos(
            repartos
                .filter { it.cantidadCentimos > 0L }
                .map { it.copy(ingresoDetectadoId = ingresoId) }
        )
    }

    suspend fun deshacerReparto(ingresoDetectado: IngresoDetectadoEntidad) {
        val usuarioId = usuarioActivoId() ?: return
        if (ingresoDetectado.deshecho) return

        val repartos = repartoDetectadoDao.obtenerRepartosPorIngreso(ingresoDetectado.id)
        repartos.forEach { reparto ->
            metaDao.restarAFondo(reparto.fondoId, usuarioId, reparto.cantidadCentimos)
        }
        ingresoDetectadoDao.marcarDeshecho(ingresoDetectado.id, usuarioId)
    }

    suspend fun reiniciarDatosUsuario() {
        val usuarioId = usuarioActivoId() ?: return
        gastoDao.borrarTodos(usuarioId)
        ingresoDao.borrarTodos(usuarioId)
        ingresoDetectadoDao.borrarTodos(usuarioId)
        metaDao.borrarTodas(usuarioId)
    }

    suspend fun cargarDatosDemoPreBeta() {
        val usuarioId = usuarioActivoId() ?: return
        reiniciarDatosUsuario()

        val viajeId = metaDao.insertarMeta(
            MetaEntidad(
                usuarioId = usuarioId,
                nombre = "Viaje",
                cantidadObjetivoCentimos = 1_200_00,
                cantidadActualCentimos = 300_00,
                fechaLimite = "31-08-2026",
                prioridad = 1
            )
        ).toInt()
        val emergenciaId = metaDao.insertarMeta(
            MetaEntidad(
                usuarioId = usuarioId,
                nombre = "Emergencia",
                cantidadObjetivoCentimos = 1_000_00,
                cantidadActualCentimos = 250_00,
                fechaLimite = "31-12-2026",
                prioridad = 1
            )
        ).toInt()
        val sofaId = metaDao.insertarMeta(
            MetaEntidad(
                usuarioId = usuarioId,
                nombre = "Sofa",
                cantidadObjetivoCentimos = 500_00,
                cantidadActualCentimos = 80_00,
                fechaLimite = "30-06-2026",
                prioridad = 2
            )
        ).toInt()

        val fecha = FechaQuri.hoyTexto()
        ingresoDao.insertarIngreso(
            IngresoEntidad(
                usuarioId = usuarioId,
                cantidadCentimos = 1_300_00,
                fecha = fecha,
                concepto = "Nomina demo"
            )
        )
        listOf(
            GastoEntidad(usuarioId = usuarioId, categoria = "Alquiler", cantidadCentimos = 520_00, fecha = fecha, etiqueta = "Necesario"),
            GastoEntidad(usuarioId = usuarioId, categoria = "Supermercado", cantidadCentimos = 180_00, fecha = fecha, etiqueta = "Necesario"),
            GastoEntidad(usuarioId = usuarioId, categoria = "Ocio", cantidadCentimos = 75_00, fecha = fecha, etiqueta = "Innecesario")
        ).forEach { gastoDao.insertarGasto(it) }

        val repartoResumen = "Viaje: 195 | Emergencia: 100 | Sofa: 125"
        val ingresoDetectadoId = ingresoDetectadoDao.insertarIngresoDetectado(
            IngresoDetectadoEntidad(
                usuarioId = usuarioId,
                concepto = "Nomina mayo detectada",
                entidad = "Banco Quri Demo",
                cantidadCentimos = 1_300_00,
                fecha = fecha,
                repartoResumen = repartoResumen,
                totalAsignadoCentimos = 420_00,
                confirmado = true
            )
        ).toInt()

        val repartos = listOf(
            RepartoDetectadoEntidad(ingresoDetectadoId = ingresoDetectadoId, fondoId = viajeId, fondoNombre = "Viaje", cantidadCentimos = 195_00),
            RepartoDetectadoEntidad(ingresoDetectadoId = ingresoDetectadoId, fondoId = emergenciaId, fondoNombre = "Emergencia", cantidadCentimos = 100_00),
            RepartoDetectadoEntidad(ingresoDetectadoId = ingresoDetectadoId, fondoId = sofaId, fondoNombre = "Sofa", cantidadCentimos = 125_00)
        )
        repartos.forEach { metaDao.aportarAFondo(it.fondoId, usuarioId, it.cantidadCentimos) }
        repartoDetectadoDao.insertarRepartos(repartos)
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


