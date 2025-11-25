package com.example.proyecto_final

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.os.CountDownTimer

class ActDescanso : AppCompatActivity() {

    private lateinit var textTimer: TextView

    private var timer: CountDownTimer? = null
    private var tiempo: Long = 10 * 60 * 1000 // 10 minutos
    private var activo = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.pantalladescanso)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaDescanso)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textTimer = findViewById(R.id.textTimerDescanso)

        actualizarTimer()  // muestra 10:00
        iniciarTimer()     // 🔥 IMPORTANTE: iniciar el descanso
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

                // 👉 Regresar al Pomodoro
                val intent = Intent(this@ActDescanso, Act5Pomodoro::class.java)
                startActivity(intent)
                finish()
            }
        }.start()

        activo = true
    }

    private fun reiniciarTimer() {
        timer?.cancel()
        tiempo = 10 * 60 * 1000
        actualizarTimer()
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
