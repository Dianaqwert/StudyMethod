package com.example.proyecto_final

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Act9JugarFC : AppCompatActivity() {

    private var listaCartas: List<Flashcard> = emptyList()
    private var indiceActual=0;
    private var mostrandoReverso=false //muestra la definicion o descripcion
    private var temaId:Int=-1

    //vistas
    private lateinit var tvTema: TextView
    private lateinit var tvCantCartas: TextView
    private lateinit var tvNumCarta: TextView
    private lateinit var tvContenido: TextView
    private lateinit var cardContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.pantallajuegoflashcards)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.juegoFC)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //tema mandado desde la otra actividad
        temaId=intent.getIntExtra("TEMA_ID",-1)
        Toast.makeText(this, "ID Recibido: $temaId", Toast.LENGTH_SHORT).show()
        //id's
        tvTema = findViewById(R.id.tvTemaTitulo)
        tvCantCartas=findViewById(R.id.tvTituloGeneral)
        tvNumCarta = findViewById(R.id.tvNumCarta)
        tvContenido = findViewById(R.id.tvContenidoCard)
        cardContainer = findViewById(R.id.cardContainer)

        val btnBien = findViewById<Button>(R.id.btnBien)
        val btnMitad = findViewById<Button>(R.id.btnMitad)
        val btnMal = findViewById<Button>(R.id.btnMal)
        val btnTerminar = findViewById<Button>(R.id.btnTerminar)


        //cargan las cartas de la base de datos
        cargarCartas()

        //animacion de giro para las cartas
        cardContainer.setOnClickListener {
            if(listaCartas.isNotEmpty()){
                girarTarjetaConAnimacion()
            }
        }

        //botones con eventlistner para guardar progreso
        btnBien.setOnClickListener {
            calificarCarta(100)
        }

        btnMitad.setOnClickListener {
            calificarCarta(50)
        }

        btnMal.setOnClickListener {
            calificarCarta(5)
        }

        //boton terminar
        btnTerminar.setOnClickListener {
            terminarEstudioAnticipado()
        }

    }

    //funciones
    private fun cargarCartas(){
        //corrutinas
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDataBase(applicationContext)
            //obtener el tema
            val temaObj = db.temaDao().getTemaById(temaId)
            val nombreTema = temaObj?.titulo ?: "Tema Desconocido"

            // obtener cartas de este tema
            var cartas=db.flashcardDao().getCartasByTema(temaId)
            //filtro de sortear
            cartas = cartas.shuffled()
            listaCartas=cartas;

            withContext(Dispatchers.Main) {
                //nombre del titulo
                tvTema.text=nombreTema
                if (listaCartas.isNotEmpty()) {
                    mostrarCartaActual()
                    tvCantCartas.text="${listaCartas.size} Cartas"
                } else {
                    tvContenido.text = "No hay cartas en este tema."
                    tvTema.text = "Tema Vacío"
                    tvCantCartas.text = "0 Cartas"
                }
            }
        }
    }

    private fun mostrarCartaActual() {
        val carta = listaCartas[indiceActual]
        mostrandoReverso = false // Reseteamos a ver el concepto

        // actualiza textos
        tvNumCarta.text = "Carta ${indiceActual + 1} de ${listaCartas.size}"
        tvContenido.text = carta.concepto
        tvContenido.textSize = 22f // Letra grande para concepto

        // la carta "vuelva" visualmente a su posición original si estaba girada
        cardContainer.rotationY = 0f
    }

    private fun girarTarjetaConAnimacion() {
        // animación simple usando ViewPropertyAnimator
        cardContainer.animate().rotationY(90f).setDuration(150).withEndAction {
            // cambiar el texto justo a la mitad de la animación
            val carta = listaCartas[indiceActual]
            if (mostrandoReverso) {
                tvContenido.text = carta.concepto
                tvContenido.textSize = 22f
                mostrandoReverso = false
            } else {
                tvContenido.text = carta.definicion
                tvContenido.textSize = 18f
                mostrandoReverso = true
            }

            // completar el giro
            cardContainer.rotationY = -90f
            cardContainer.animate().rotationY(0f).setDuration(150).start()
        }.start()
    }

    private fun calificarCarta(porcentaje: Int) {
        if (listaCartas.isEmpty()) return

        val cartaActual = listaCartas[indiceActual]

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDataBase(applicationContext)
            // guarda el porcentaje en la BD
            db.flashcardDao().updateCardProgress(cartaActual.id, porcentaje)

            // calcular nuevo progreso del TEMA (Promedio)
            val cartasActualizadas = db.flashcardDao().getCartasByTema(temaId)
            var sumaPorcentajes = 0
            for (c in cartasActualizadas) { sumaPorcentajes += c.porcentajeAprendizaje }
            val nuevoPromedioTema = sumaPorcentajes / cartasActualizadas.size

            // actualiza tabla Temas en TemaDao
            db.temaDao().updateTemaProgress(temaId, nuevoPromedioTema)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@Act9JugarFC, "Guardado: $porcentaje%", Toast.LENGTH_SHORT).show()
                avanzarCarta()
            }
        }
    }

    private fun avanzarCarta() {
        if (indiceActual < listaCartas.size - 1) {
            indiceActual++
            mostrarCartaActual()
        } else {
            Toast.makeText(this, "¡Fin del repaso!", Toast.LENGTH_LONG).show()
            finish() // cierra la actividad y vuelve al menú
        }
    }

    private fun terminarEstudioAnticipado() {
        //como se interrumoe el juego el promedio general del tema esté actualizado con las cartas que SÍ jugó.
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDataBase(applicationContext)

            // trae todas las cartas del tema
            val todasLasCartas = db.flashcardDao().getCartasByTema(temaId)

            // calculo del promedio real actual
            var sumaPorcentajes = 0
            var cartasJugadas = 0

            for (carta in todasLasCartas) {
                // solo sumamos si la carta tiene un porcentaje mayor a 0 (si ya fue jugada)
                sumaPorcentajes += carta.porcentajeAprendizaje

            }

            //para el progreso de cada tema
            val promedioGlobal = if (todasLasCartas.isNotEmpty()) {
                sumaPorcentajes / todasLasCartas.size
            } else {
                0
            }

            // actualiza el tema
            db.temaDao().updateTemaProgress(temaId, promedioGlobal)

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@Act9JugarFC,
                    "Estudio finalizado. Progreso guardado.",
                    Toast.LENGTH_SHORT
                ).show()
                finish() // cierra la actividad y vuelve al menú
            }
        }
    }


}