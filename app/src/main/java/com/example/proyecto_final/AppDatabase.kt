package com.example.proyecto_final

import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Database
import androidx.room.Room

//Se definen las entidades o tablas de la BD
@Database(entities = [User::class, Tema::class, Flashcard::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){
    //DAOS  utilizar
    //funciones que se se utilizan para guardar y leer usuarios
    abstract fun userDao(): UserDao
    abstract fun temaDao(): TemaDao
    abstract fun flashcardDao(): FlashcardDao
    //ahorra memoria
    companion object{
        @Volatile
        private var INSTANCE: AppDatabase?=null
        fun getDataBase(context: Context): AppDatabase{
            return INSTANCE ?:synchronized(this){
                val instance =Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "proyecto_database_StudyMethod" //nombre de la BD en el celular
                ).build()
                INSTANCE=instance
                instance
            }
        }
    }
}