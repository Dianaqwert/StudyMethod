package com.example.proyecto_final
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

//Maneja las operaciones del usuario
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarUser(user: User): Long

    //funcion para verificar si el usuario ya existe
    @Query("SELECT * FROM users WHERE email=:email AND contrasena=:contrasena LIMIT 1")
    suspend fun checkLogin(email: String,contrasena:String):User?
}