package com.example.proyecto_final

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Act8Estadisticas : AppCompatActivity() {
    private var temaId: Int = -1
    private lateinit var tvTitulo: TextView
    private lateinit var tvCant100: TextView
    private lateinit var tvCant50: TextView
    private lateinit var tvCant5: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.pantallaestadiscticasflashcards)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaEstd)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // id's
        tvTitulo = findViewById(R.id.tvTituloTemaEstadistica)
        tvCant100 = findViewById(R.id.cantidad100)
        tvCant50 = findViewById(R.id.cantidad50)
        tvCant5 = findViewById(R.id.cantidad5)
        val btnRepasar = findViewById<Button>(R.id.btnRepasar)

        temaId = intent.getIntExtra("TEMA_ID", -1)

        // boton de repaso
        btnRepasar.setOnClickListener {
            val intent = Intent(this, Act9JugarFC::class.java)
            intent.putExtra("TEMA_ID", temaId)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        //se obtienen los datos de la BD
        if (temaId != -1) {
            cargarEstadisticas()
        } else {
            // verifica que tvTitulo esté inicializado antes de usarlo
            if (::tvTitulo.isInitialized) {
                tvTitulo.text = "Error: Tema no encontrado"
            }
        }
    }

    private fun cargarEstadisticas() {
        lifecycleScope.launch(Dispatchers.IO) {
            //BD
            val db = AppDatabase.getDataBase(applicationContext)
            val flashcardDao = db.flashcardDao()
            val temaDao = db.temaDao()

            //nombre del tema
            val tema = temaDao.getTemaById(temaId)

            //cuenta las cartas por progreso
            val total100 = flashcardDao.countByProgress(temaId, 100)
            val total50 = flashcardDao.countByProgress(temaId, 50)
            // suma de las de 5% a 0%
            val total5 = flashcardDao.countByProgress(temaId, 5) + flashcardDao.countByProgress(temaId, 0)

            withContext(Dispatchers.Main) {
                // actualiza UI
                tvTitulo.text = tema?.titulo ?: "Tema Desconocido"
                tvCant100.text = "Cartas aprendidas: $total100"
                tvCant50.text = "Cartas en proceso: $total50"
                tvCant5.text = "Cartas difíciles: $total5"
            }
        }
    }
}