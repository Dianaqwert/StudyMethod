package com.example.proyecto_final

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
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

class Act6MenuFC : AppCompatActivity() {
    //para el manejo de la vista de los views que se muestran al agregar temas
    private lateinit var contenedorTemas: LinearLayout
    private lateinit var contenedorErrores: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.pmenuflashcards)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pmenufc)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // configurar Usuario (SharedPreferences)
        val sharedPref = getSharedPreferences("MiAppPrefs", Context.MODE_PRIVATE)
        val nombreUsuario = sharedPref.getString("user_name", "Usuario")
        val txtNombre = findViewById<TextView>(R.id.txtNombreUsuario)
        txtNombre.text = nombreUsuario

        // vincular los contenedores vacíos del XML
        contenedorTemas = findViewById(R.id.contenedorCuadros)
        contenedorErrores = findViewById(R.id.contenedorDeCuadros)

        // para agregar cartas
        val btnAddC: Button = findViewById(R.id.btnAddCartas)

        //listener para ir a agregar tema
        btnAddC.setOnClickListener {
            val intent = Intent(this, Act10addTema::class.java)
            startActivity(intent)
        }

    }

    // se ejecuta cada vez que la pantalla se vuelve visible
    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    private fun cargarDatos() {
        lifecycleScope.launch(Dispatchers.IO) {
            // acceso a la base de datos
            val db = AppDatabase.getDataBase(applicationContext)
            val temas = db.temaDao().getAllTemas()

            withContext(Dispatchers.Main) {
                // limpia las vistas viejas
                contenedorTemas.removeAllViews()
                contenedorErrores.removeAllViews()

                if (temas.isEmpty()) {
                    mostrarMensajeVacio()
                } else {
                    for (tema in temas) {
                        // por cada tema encontrado:
                        //se agregamos al carrusel horizontal
                        agregarVistaTema(tema)
                        // se agrega a la vista vertical (estadísticas)
                        if (tema.progreso > 0 || true) { //true es para asegurarse
                            agregarVistaEstadistica(tema)
                        }
                    }
                }
            }
        }
    }

    // funcion para crear el cuadro de las estadisticas por tema(Scroll Horizontal)
    private fun agregarVistaTema(tema: Tema) {
        val view = LayoutInflater.from(this).inflate(R.layout.item_tema_fc, contenedorTemas, false)

        val btnJugar = view.findViewById<Button>(R.id.btnJugarFC_Item)
        btnJugar.text = "#${tema.id} ${tema.titulo}"
        //clic normal : jugar
        btnJugar.setOnClickListener {
            val intent = Intent(this, Act9JugarFC::class.java)
            intent.putExtra("TEMA_ID", tema.id)
            startActivity(intent)
        }
        //clic largo : elimina el tema
        btnJugar.setOnClickListener {
            mostrarMsjEliminar(tema)
            true //se consume el evento y no hace el clic normal despues
        }

        contenedorTemas.addView(view)
    }

    // función para crear el cuadro azul con el que se indica que se creo el tema (Scroll Vertical)
    private fun agregarVistaEstadistica(tema: Tema) {
        val view = LayoutInflater.from(this).inflate(R.layout.item_estadistica_fc, contenedorErrores, false)

        val txtTitulo = view.findViewById<TextView>(R.id.txtTituloEstadistica)
        val txtDetalle = view.findViewById<TextView>(R.id.txtDetalleEstadistica)
        val btnVerMas = view.findViewById<Button>(R.id.btnVerMasEstadistica)

        txtTitulo.text = "#${tema.id} ${tema.titulo}"
        txtDetalle.text = "Progreso: ${tema.progreso}%"

        btnVerMas.setOnClickListener {
            val intent = Intent(this, Act8Estadisticas::class.java)
            intent.putExtra("TEMA_ID", tema.id)
            startActivity(intent)
        }

        contenedorErrores.addView(view)
    }

    private fun mostrarMensajeVacio() {
        //limpiamos ambos contenedores para que no se encimen cosas
        contenedorTemas.removeAllViews()
        contenedorErrores.removeAllViews()

        // inflamos el LAYOUT (no el drawable)
        val vistaVacia = LayoutInflater.from(this).inflate(R.layout.layout_vacio, contenedorErrores, false)

        // agregamos al contenedor de errores (el grande vertical)
        contenedorErrores.addView(vistaVacia)

        val txt = TextView(this)
        txt.text = "Sin temas..."
        txt.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.naranja))
        txt.textSize = 18f
        txt.setPadding(20, 20, 20, 20)
        val typeface = androidx.core.content.res.ResourcesCompat.getFont(this, R.font.aoboshi_one)
        txt.typeface = typeface

        contenedorTemas.addView(txt)
    }

    private fun mostrarMsjEliminar(tema: Tema) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Eliminar Tema")
        builder.setMessage("¿Estás seguro de borrar el tema '${tema.titulo}' y todas sus cartas?")

        // boton con (SI)
        builder.setPositiveButton("Sí, borrar") { dialog, _ ->
            eliminarTemaDeBD(tema)
            dialog.dismiss()
        }

        // boton con(NO)
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val alert = builder.create()
        alert.show()
    }

    private fun eliminarTemaDeBD(tema: Tema) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDataBase(applicationContext)

            // borramos el tema (y sus cartas se borran)
            db.temaDao().deleteTema(tema)

            withContext(Dispatchers.Main) {
                //se cargan nuevamente los datos
                cargarDatos()
                Toast.makeText(this@Act6MenuFC, "Tema eliminado", Toast.LENGTH_SHORT).show()
            }
        }
    }

}