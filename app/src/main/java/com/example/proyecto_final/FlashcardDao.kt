package com.example.proyecto_final

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
@Dao
interface FlashcardDao {
    //insertar cartas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: Flashcard)

    //para traer las cartas
    @Query("SELECT * FROM flashcards WHERE temaId=:idTema")
    suspend fun getCartasByTema(idTema: Int):List<Flashcard>

    // actualiza solo el porcentaje de una carta
    @Query("UPDATE flashcards SET porcentajeAprendizaje = :porcentaje WHERE id = :cardId")
    suspend fun updateCardProgress(cardId: Int, porcentaje: Int)

    //cuenta las cartas que tiene un porcentaje especifico
    @Query("SELECT COUNT(*) FROM flashcards WHERE temaId=:temaId AND porcentajeAprendizaje=:porcentaje")
    suspend fun countByProgress(temaId: Int, porcentaje: Int): Int
}