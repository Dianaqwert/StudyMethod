package com.example.proyecto_final

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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

class Act11NuevasCartas : AppCompatActivity() {
    //conteo de las cartas
    private var cartasAgregadas=0
    private var cantidadObjetivo=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.agregarcartasterminos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cartas)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //datos registrados en ACT11
        val temaId = intent.getIntExtra("TEMA_ID", -1)
        val tituloTema = intent.getStringExtra("TEMA_TITULO")
        cantidadObjetivo=intent.getIntExtra("CANTIDAD_OBJETIVO",0)//CONTEO
        //ID's
        val tvIdTema = findViewById<TextView>(R.id.idTema)
        val btnName = findViewById<Button>(R.id.btnJugarFC)
        val tvNumeroCarta = findViewById<TextView>(R.id.numeroCarta)
        val etConcepto = findViewById<EditText>(R.id.conceptoFC)
        val etDescrip = findViewById<EditText>(R.id.descripCarta)
        val btnAgregar = findViewById<Button>(R.id.btnAddNuevas)

        //configuraciones del texto
        tvIdTema.text="Tema:#$temaId"
        btnName.text=tituloTema
        actualizarContador(tvNumeroCarta)
        //base de datos
        val db= AppDatabase.getDataBase(this)
        val flashcardDao=db.flashcardDao()

        //listener del boton al agregar cartas y corrutinas
        btnAgregar.setOnClickListener {

            val concepto=etConcepto.text.toString().trim()
            val definicion=etDescrip.text.toString().trim()

            //validacion
            if(concepto.isNotEmpty() && definicion.isNotEmpty()){
                //corrutina
                lifecycleScope.launch(Dispatchers.IO) {
                    //guardar carta -> un objeto
                    val nuevasCarta= Flashcard(
                        temaId=temaId,
                        concepto = concepto,
                        definicion = definicion,
                        porcentajeAprendizaje = 0
                    )
                    flashcardDao.insertCard(nuevasCarta)

                    //actualizar UI
                    withContext(Dispatchers.Main){
                        cartasAgregadas++
                        Toast.makeText(this@Act11NuevasCartas,"Carta guardada", Toast.LENGTH_SHORT).show()
                        //limpiar registros
                        etConcepto.text.clear()
                        etDescrip.text.clear()
                        etConcepto.requestFocus()//pone el cursor

                        //validación con respecto a la cantidad dada
                        if(cartasAgregadas>=cantidadObjetivo){
                            Toast.makeText(this@Act11NuevasCartas, "Tema completado ✨", Toast.LENGTH_SHORT).show()
                            //mandamos al menu de flashcards
                            //val intent = Intent(this@Act11NuevasCartas, Act6MenuFC::class.java)
                            finish()
                        }else{
                            //se actualiza el contador
                            actualizarContador(tvNumeroCarta);
                        }
                    }
                }
            }else{
                Toast.makeText(this, "LLena ambos campos ⚠️", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun actualizarContador(textView: TextView){
        val actual=cartasAgregadas+1
        textView.text="Carta número $actual de $cantidadObjetivo"
    }
}