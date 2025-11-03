package com.example.proyecto_final

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Activity4 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.p4)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.p4)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnFC: Button=findViewById(R.id.btnFlashCards);
        val btnPom: Button=findViewById(R.id.btnPomodoro);
        val btnCT: Button=findViewById(R.id.btnCT);

        //listener
        btnPom.setOnClickListener {
            //INTENT
            val intent = Intent(this, Act5Pomodoro::class.java)
            //inicia la actividad
            startActivity(intent)
        }

        //listener
        btnFC.setOnClickListener {
            //INTENT
            val intent = Intent(this, Act6MenuFC::class.java)
            //inicia la actividad
            startActivity(intent)
        }

        //listener
        btnCT.setOnClickListener {
            //INTENT
            val intent = Intent(this, Act7ChatTutor::class.java)
            //inicia la actividad
            startActivity(intent)
        }


    }
}