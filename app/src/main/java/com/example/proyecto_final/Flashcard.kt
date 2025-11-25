package com.example.proyecto_final

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "flashcards",
    //para la relacion de 1 tema con varias flashcards
    foreignKeys = [
        ForeignKey(
        entity = Tema::class,
        parentColumns = ["id"],
        childColumns = ["temaId"],
        onDelete = ForeignKey.CASCADE //si se borra el tema , se borran las cartas
    )]
)
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id:Int=0,
    //se guarda el id del tema padre
    val temaId:Int, // conexion con la tabla del tema
    val concepto: String,
    val definicion: String,
    //estadisticas individuales
    val porcentajeAprendizaje:Int=0
)
