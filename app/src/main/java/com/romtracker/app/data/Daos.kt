package com.romtracker.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

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

    /** Borra todos los días de un hábito (al borrar un hábito propio). */
    @Query("DELETE FROM registro WHERE habito = :habito")
    suspend fun borrarDeHabito(habito: String)
}

@Dao
interface AguaDao {
    @Query("SELECT * FROM agua")
    suspend fun todas(): List<Agua>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(agua: Agua)

    /** Volver a "sin dato". */
    @Query("DELETE FROM agua WHERE fecha = :fecha")
    suspend fun borrar(fecha: Long)

    @Query("DELETE FROM agua")
    suspend fun borrarTodo()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(aguas: List<Agua>)
}

@Dao
interface AnimoDao {
    @Query("SELECT * FROM animo")
    suspend fun todos(): List<Animo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(animo: Animo)

    /** Volver a "sin dato". */
    @Query("DELETE FROM animo WHERE fecha = :fecha")
    suspend fun borrar(fecha: Long)

    @Query("DELETE FROM animo")
    suspend fun borrarTodo()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(animos: List<Animo>)
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

@Dao
interface ChecklistItemDao {
    @Query("SELECT * FROM checklist_item ORDER BY orden, id")
    suspend fun todos(): List<ChecklistItem>

    /** Devuelve el id nuevo. */
    @Insert
    suspend fun crear(item: ChecklistItem): Long

    @Query("UPDATE checklist_item SET texto = :texto WHERE id = :id")
    suspend fun renombrar(id: Long, texto: String)

    @Query("DELETE FROM checklist_item WHERE id = :id")
    suspend fun borrar(id: Long)

    @Query("DELETE FROM checklist_item")
    suspend fun borrarTodo()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(items: List<ChecklistItem>)
}

@Dao
interface ChecklistMarcaDao {
    @Query("SELECT * FROM checklist_marca WHERE paraFecha = :paraFecha")
    suspend fun deDia(paraFecha: Long): List<ChecklistMarca>

    @Query("SELECT * FROM checklist_marca")
    suspend fun todas(): List<ChecklistMarca>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(marca: ChecklistMarca)

    /** Al borrar un ítem se borran también sus marcas. */
    @Query("DELETE FROM checklist_marca WHERE itemId = :itemId")
    suspend fun borrarDeItem(itemId: Long)

    @Query("DELETE FROM checklist_marca")
    suspend fun borrarTodo()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(marcas: List<ChecklistMarca>)
}

@Dao
interface HabitoPropioDao {
    @Query("SELECT * FROM habito_propio ORDER BY id")
    suspend fun todos(): List<HabitoPropio>

    /** Devuelve el id nuevo. */
    @Insert
    suspend fun crear(habito: HabitoPropio): Long

    @Update
    suspend fun actualizar(habito: HabitoPropio)

    @Query("DELETE FROM habito_propio WHERE id = :id")
    suspend fun borrar(id: Long)

    @Query("DELETE FROM habito_propio")
    suspend fun borrarTodo()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(habitos: List<HabitoPropio>)
}

@Dao
interface AutomatismoDao {
    @Query("SELECT * FROM automatismo ORDER BY fecha")
    suspend fun todos(): List<Automatismo>

    /** Un check por hábito y día: si ya había uno ese día, se reemplaza. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(check: Automatismo)

    /** Al borrar un hábito propio se borran también sus checks. */
    @Query("DELETE FROM automatismo WHERE habito = :habito")
    suspend fun borrarDeHabito(habito: String)

    @Query("DELETE FROM automatismo")
    suspend fun borrarTodo()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(checks: List<Automatismo>)
}

@Dao
interface RevisionDao {
    @Query("SELECT * FROM revision ORDER BY semana")
    suspend fun todas(): List<Revision>

    /** Una revisión por semana: si ya había una, se reemplaza. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(revision: Revision)

    @Query("DELETE FROM revision")
    suspend fun borrarTodo()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(revisiones: List<Revision>)
}
