package com.example.proyecto_final
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Random

class MainActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var contenedorAnimacion: FrameLayout
    private var animacionActiva = true
    private val random = Random()

    private val simbolos = listOf("+", "-", "Flashcards", "÷", "=", "%", "√","ChatTutor","Pomodoro")

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

        contenedorAnimacion = findViewById(R.id.contenedorAnimacion)

        // animación
        comenzarLluviaMatematica()

        //listener
        btnlogIn.setOnClickListener {
            //INTENT
            val intent = Intent(this, Activity2::class.java)
            //inicia la actividad
            startActivity(intent)
        }


    }

    private fun comenzarLluviaMatematica() {
        // se ejecutará repetidamente
        val runnable = object : Runnable {
            override fun run() {
                if (animacionActiva) {
                    crearSimboloFlotante()
                    // crea un nuevo símbolo cada 300ms
                    handler.postDelayed(this, 300)
                }
            }
        }
        handler.post(runnable)
    }

    private fun crearSimboloFlotante() {
        //TextView
        val txtSimbolo = TextView(this)
        txtSimbolo.text = simbolos[random.nextInt(simbolos.size)]

        // Estilo del símbolo
        txtSimbolo.textSize = 20f + random.nextInt(30) // tamaño aleatorio entre 20 y 50
        txtSimbolo.setTextColor(Color.parseColor("#638c80"))

        // posición inicial (Abajo de la pantalla, posición X aleatoria)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels

        txtSimbolo.x = random.nextFloat() * screenWidth // cualquier lugar horizontalmente
        txtSimbolo.y = screenHeight.toFloat()

        contenedorAnimacion.addView(txtSimbolo, params)

        // (Mover hacia arriba y rotar)
        val duracion = 3000L + random.nextInt(3000) // entre 3 y 6 segundos para subir

        txtSimbolo.animate()
            .translationY(-200f) // mover hasta arriba (y un poco más para que salga)
            .rotation(random.nextInt(360).toFloat()) // rotar aleatoriamente
            .alpha(0f) // desvanecerse al final
            .setDuration(duracion)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction {
                // eliminar la vista cuando termine para no llenar la memoria
                contenedorAnimacion.removeView(txtSimbolo)
            }
            .start()
    }

    override fun onDestroy() {
        super.onDestroy()
        // detener la animación si cerramos la app para evitar errores
        animacionActiva = false
        handler.removeCallbacksAndMessages(null)
    }

    override fun onPause() {
        super.onPause()
        // pausar si sale momentáneamente
        animacionActiva = false
    }

    override fun onResume() {
        super.onResume()
        if (!animacionActiva) {
            animacionActiva = true
            comenzarLluviaMatematica()
        }
    }
}