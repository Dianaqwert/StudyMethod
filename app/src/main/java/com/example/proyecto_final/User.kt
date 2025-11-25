package com.example.proyecto_final
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

//tabla donde se guarda los usuarios
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id:Int=0,
    @ColumnInfo(name="nombre") val nombre: String,
    @ColumnInfo(name="email") val email: String,
    @ColumnInfo(name="contrasena") val contrasena: String,
    )
