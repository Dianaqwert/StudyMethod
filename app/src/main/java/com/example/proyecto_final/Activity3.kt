package com.example.proyecto_final

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        val btnlogIn: Button=findViewById(R.id.btnContinuar);

        //listener
        btnlogIn.setOnClickListener {
            //INTENT
            val intent = Intent(this, Activity4::class.java)
            //inicia la actividad
            startActivity(intent)
        }

    }
}