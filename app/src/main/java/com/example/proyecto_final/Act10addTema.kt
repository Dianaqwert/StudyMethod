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


class Act10addTema : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.agregarcartasconjuntos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.agregarCartas)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //se identifican los id
        val etTema = findViewById<EditText>(R.id.textTema)
        val etCantidad = findViewById<EditText>(R.id.textCantidad)
        val btnAddNuevas: Button = findViewById(R.id.btnAddNuevas);
        //base de datos
        val db = AppDatabase.getDataBase(this)
        val temaDao=db.temaDao()

        //listener
        btnAddNuevas.setOnClickListener {

            //agregar tema
            val titulo=etTema.text.toString().trim()
            val cantidadStr=etCantidad.text.toString().trim()

            if(titulo.isNotEmpty()&& cantidadStr.isNotEmpty()){
                val cantidad=cantidadStr.toIntOrNull()?:0

                lifecycleScope.launch(Dispatchers.IO) {
                    //crear el objeto
                    val nuevoTema=Tema(
                        titulo=titulo,
                        cantidadObjetivo =cantidad,
                        progreso = 0//inicia en 0
                    )

                    //inserta y se obtiene el id
                    val idNuevoTema=temaDao.insertTema(nuevoTema)
                    //se manda a activity11 para agregar las nuevas cartas
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@Act10addTema, "Tema '$titulo' creado", Toast.LENGTH_SHORT).show()
                        //INTENT
                        val intent = Intent(this@Act10addTema, Act11NuevasCartas::class.java)
                        //pasa el id y titulo
                        intent.putExtra("TEMA_ID",idNuevoTema.toInt())
                        intent.putExtra("TEMA_TITULO",titulo)
                        intent.putExtra("CANTIDAD_OBJETIVO",cantidad)
                        //inicia la actividad
                        startActivity(intent)
                        //se cierra el formulario?
                        finish()

                    }
                }
            }else{
                Toast.makeText(this, "Por favor escribe un tema y cantidad", Toast.LENGTH_SHORT).show()
            }

        }

    }
}