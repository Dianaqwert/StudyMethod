package com.example.proyecto_final

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyecto_final.R
import android.widget.Button
import android.widget.TextView
import android.os.CountDownTimer
import android.content.Intent

class Act5Pomodoro : AppCompatActivity() {

    private lateinit var textTimer: TextView
    private lateinit var btnComenzar: Button

    private var timer: CountDownTimer? = null
    private var tiempo: Long = 1 * 60 * 1000 // 25 minutos en milisegundos
    private var activo = false // indica si el cronómetro está corriendo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.pantallapomodoro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pomodoro)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textTimer = findViewById(R.id.textTimer)
        btnComenzar = findViewById(R.id.btnComenzar)

        actualizarTimer() //muestra nuevamente 25:00

        btnComenzar.setOnClickListener {
            if (!activo) {
                iniciarTimer()
            } else {
                reiniciarTimer()
            }
        }
    }

    private fun iniciarTimer() {
        timer = object : CountDownTimer(tiempo, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tiempo = millisUntilFinished
                actualizarTimer()
            }

            override fun onFinish() {
                activo = false
                textTimer.text = "00:00"
                btnComenzar.text = "REINICIAR"
                val intent = Intent(this@Act5Pomodoro, ActDescanso::class.java)
                startActivity(intent)

                // Opcional: cerrar esta Activity para que no se pueda volver con "Back"
                finish()
            }
        }.start()

        activo = true
        btnComenzar.text = "DETENER"
    }

    private fun reiniciarTimer() {
        timer?.cancel()
        tiempo = 25 * 60 * 1000
        actualizarTimer()
        btnComenzar.text = "COMENZAR"
        activo = false
    }

    private fun actualizarTimer() {
        val minutos = (tiempo / 1000) / 60
        val segundos = (tiempo / 1000) % 60
        val tiempoAct = String.format("%02d:%02d", minutos, segundos)
        textTimer.text = tiempoAct
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}