package com.example.quritfg.datos.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * DAO encargado de todo lo relacionado con usuarios.
 *
 * Aqui se definen las operaciones basicas como:
 * guardar un usuario o comprobar login.
 *
 * Room usa esto para generar el codigo automaticamente,
 * asi que no hay implementacion manual.
 */
@Dao
interface UsuarioDao {

    /**
     * Inserta un usuario en la base de datos.
     */
    @Insert
    suspend fun insertarUsuario(usuario: UsuarioEntidad)

    /**
     * Comprueba si existe un usuario con ese email y password.
     *
     * Si coincide devuelve el usuario, si no devuelve null.
     * Basicamente esto es lo que se usa para hacer login.
     */
    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UsuarioEntidad?

    /**
     * Busca un usuario solo por su email.
     *
     * util para validar si ya existe antes de registrarlo
     * o para recuperar sus datos sin hacer login.
     */
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun obtenerUsuarioPorEmail(email: String): UsuarioEntidad?
}