package com.example.proyecto_final

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.p1)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.p1xml)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnlogIn: ImageButton=findViewById(R.id.btnLogin);

        //listener
        btnlogIn.setOnClickListener {
            //INTENT
            val intent = Intent(this, Activity2::class.java)
            //inicia la actividad
            startActivity(intent)
        }


    }
}