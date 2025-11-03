package com.example.proyecto_final

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Act6MenuFC : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.pmenuflashcards)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pmenufc)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnEstadisticas: Button=findViewById(R.id.btnEstadisticas);
        val btnJugar: Button=findViewById(R.id.btnJugarFC)
        val btnAddC:Button=findViewById(R.id.btnAddCartas);

        //listener
        btnEstadisticas.setOnClickListener {
            //INTENT
            val intent = Intent(this, Act8Estadisticas::class.java)
            //inicia la actividad
            startActivity(intent)
        }

        btnJugar.setOnClickListener {
            //INTENT
            val intent = Intent(this, Act9JugarFC::class.java)
            //inicia la actividad
            startActivity(intent)
        }

        btnAddC.setOnClickListener {
            //INTENT
            val intent = Intent(this, Act10addTema::class.java)
            //inicia la actividad
            startActivity(intent)
        }

    }
}