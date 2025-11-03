package com.example.proyecto_final

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        val btnAddNuevas: Button = findViewById(R.id.btnAddNuevas);

        //listener
        btnAddNuevas.setOnClickListener {
            //INTENT
            val intent = Intent(this, Act11NuevasCartas::class.java)
            //inicia la actividad
            startActivity(intent)
        }

    }
}