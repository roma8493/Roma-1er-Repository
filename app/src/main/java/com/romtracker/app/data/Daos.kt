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
}

@Dao
interface HabitoDao {
    @Query("SELECT * FROM habito WHERE tipo = :tipo")
    suspend fun obtener(tipo: String): Habito?

    /** No pisa el inicio si el hábito ya existe. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun crear(habito: Habito)
}

@Dao
interface RegistroDao {
    @Query("SELECT * FROM registro WHERE habito = :habito")
    suspend fun todos(habito: String): List<Registro>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(registro: Registro)

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
}
