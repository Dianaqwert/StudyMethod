package com.example.proyecto_final

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "temas")
data class Tema(
    @PrimaryKey(autoGenerate = true) val id:Int=0,
    @ColumnInfo(name="titulo") val titulo:String,
    //cantidad de cartas
    @ColumnInfo(name="cantidad_objetivo") val cantidadObjetivo:Int,
    //estadisticas generales del tema -> se calculan sumando las cartas
    @ColumnInfo(name="progreso_tema")val progreso:Int=0// va del 0 al 100%
)
