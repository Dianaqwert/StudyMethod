package com.example.proyecto_final
import androidx.room.*

@Dao
interface TemaDao {
    // guardar el tema y devolver el ID generado (IMPORTANTE para pasar a la sig actividad)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTema(tema: Tema): Long

    // obtener todos los temas para listarlos en el menú
    @Query("SELECT * FROM temas")
    suspend fun getAllTemas(): List<Tema>

    // borrar un tema (borrará sus cartas automáticamente por el CASCADE)
    @Delete
    suspend fun deleteTema(tema: Tema)
    //Buscar un tema por su ID
    @Query("SELECT * FROM temas WHERE id = :id LIMIT 1")
    suspend fun getTemaById(id: Int): Tema?

    //actualiza el promedio global del tema
    @Query("UPDATE temas SET progreso_tema = :progreso WHERE id = :temaId")
    suspend fun updateTemaProgress(temaId: Int, progreso: Int)
}