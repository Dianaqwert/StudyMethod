package com.example.proyecto_final

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Context
class Activity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.p3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.p3)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //se verifica la sesion por medio de preferencias
        val sharedPref = getSharedPreferences("MiAppPrefs", Context.MODE_PRIVATE)
        val yaSeRegistro=sharedPref.getBoolean("is_registered",false)

        if(yaSeRegistro){
            //se deridige a la activity4
            val intent= Intent(this, Activity4::class.java)
            startActivity(intent)
            //se cierra para que esta actividad no se vea
            finish()
            return //se detiene la ejecucion del resto del código
        }

        //id-a identificar
        val btnlogIn: Button=findViewById(R.id.btnContinuar)
        val etNombre = findViewById<EditText>(R.id.txtNombreUsr)
        val eEmail=findViewById<EditText>(R.id.email)
        val eContrasena=findViewById<EditText>(R.id.contrasena)

        //instancia para la base de datos
        val db = AppDatabase.getDataBase(this)
        //obtiene el DAO para guardar cosas
        val userDao = db.userDao()

        //listener
        btnlogIn.setOnClickListener {
            //obtener el texto de los campos
            val nombreTxt=etNombre.text.toString().trim()
            val emailTxt=eEmail.text.toString().trim()
            val contraTxt=eContrasena.text.toString().trim()

            //validacion para evitar campos vacios
            if(nombreTxt.isNotEmpty() &&
                emailTxt.isNotEmpty() &&
                contraTxt.isNotEmpty()){
                //inicia corrutina para guardar en la BD
                lifecycleScope.launch(Dispatchers.IO){
                    //se crea un objeto de usuario
                    val nuevoUsuario=User(
                        nombre=nombreTxt,
                        email = emailTxt,
                        contrasena = contraTxt
                    )

                    //insertar en la BD
                    userDao.insertarUser(nuevoUsuario)
                    //se manda otra vez al hilo principal para el cambio de pantalla
                    withContext(Dispatchers.Main){
                        //se guarda el estado de que ya se registro el usuario
                        val bandera=sharedPref.edit()
                        bandera.putBoolean("is_registered",true) //se le indica que el usuario ya se registro
                        //se guarda el nombre para usarlo
                        bandera.putString("user_name", nombreTxt)
                        bandera.apply() //guarda cambios

                        Toast.makeText(this@Activity3,"Usuario guardado correctamente",
                            Toast.LENGTH_SHORT).show()
                        //manda a la siguiente actividad
                        val intent = Intent(this@Activity3, Activity4::class.java)
                        startActivity(intent)
                        finish()//evita volver al registro
                    }
                }
            }else{
                //si falta algun dato
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

    }
}