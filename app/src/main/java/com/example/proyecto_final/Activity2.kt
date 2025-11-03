package com.example.proyecto_final

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Activity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.p2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.p2)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnlogIn: Button=findViewById(R.id.btnComenzar);

        //listener
        btnlogIn.setOnClickListener {
            //INTENT
            val intent = Intent(this, Activity3::class.java)
            //inicia la actividad
            startActivity(intent)
        }

    }
}