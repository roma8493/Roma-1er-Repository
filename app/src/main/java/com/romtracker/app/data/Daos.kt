package com.romtracker.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PerfilDao {
    @Query("SELECT * FROM perfil WHERE id = 1")
    suspend fun obtener(): Perfil?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(perfil: Perfil)

    @Query("DELETE FROM perfil")
    suspend fun borrarTodo()
}

@Dao
interface HabitoDao {
    @Query("SELECT * FROM habito WHERE tipo = :tipo")
    suspend fun obtener(tipo: String): Habito?

    /** No pisa el inicio si el hábito ya existe. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun crear(habito: Habito)

    @Query("SELECT * FROM habito")
    suspend fun todos(): List<Habito>

    @Query("DELETE FROM habito")
    suspend fun borrarTodo()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(habitos: List<Habito>)
}

@Dao
interface RegistroDao {
    @Query("SELECT * FROM registro WHERE habito = :habito")
    suspend fun todos(habito: String): List<Registro>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(registro: Registro)

    @Query("SELECT * FROM registro")
    suspend fun todosLosRegistros(): List<Registro>

    @Query("DELETE FROM registro")
    suspend fun borrarTodo()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(registros: List<Registro>)

    /** Volver a "sin dato". */
    @Query("DELETE FROM registro WHERE habito = :habito AND fecha = :fecha")
    suspend fun borrar(habito: String, fecha: Long)
}

@Dao
interface ChecklistDao {
    @Query("SELECT * FROM checklist WHERE paraFecha = :paraFecha")
    suspend fun obtener(paraFecha: Long): Checklist?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(checklist: Checklist)

    @Query("SELECT * FROM checklist")
    suspend fun todas(): List<Checklist>

    @Query("DELETE FROM checklist")
    suspend fun borrarTodo()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(checklists: List<Checklist>)
}
