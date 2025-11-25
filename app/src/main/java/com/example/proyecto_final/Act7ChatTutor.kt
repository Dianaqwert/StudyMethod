/*package com.example.proyecto_final
import android.widget.LinearLayout
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class Act7ChatTutor : AppCompatActivity() {
    private val apiKey = BuildConfig.OPENAI_API_KEY
    //variable de tipo OpenAICliente para poder hacer uso del chat
    private lateinit var openAIClient: OpenAIClient
    //lateinit : inicializara mas tarde
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.pantallachat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chatTutor)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //texto de id para el user msg
        val editTextMessage = findViewById<EditText>(R.id.editTextMessage)
        val sendButton = findViewById<ImageButton>(R.id.imageButtonSend)
        val chatContainer = findViewById<LinearLayout>(R.id.chatAreaContainer)

        openAIClient = OpenAIClient(BuildConfig.OPENAI_API_KEY)

        openAIClient.sendMessage("Explícame qué es la fotosíntesis") { response ->
            runOnUiThread {
                // Elimina la línea de error: editTextMessage.text = response
                val reply = response ?: "Error: Sin respuesta"
                // Muestra la respuesta en el chat.
                addMessageToChat(chatContainer, "Tutor: $reply", false)
            }
        }

        sendButton.setOnClickListener {
           val userMessage = editTextMessage.text.toString().trim()
           if (userMessage.isNotEmpty()) {
               // Muestra el mensaje del usuario
               addMessageToChat(chatContainer, "Tú: $userMessage", true)

               // Limpia el texto
               editTextMessage.text.clear()

               // Envía al modelo
               openAIClient.sendMessage(userMessage) { response ->
                   runOnUiThread {
                       val reply = response ?: "Error o sin respuesta"
                       addMessageToChat(chatContainer, "Tutor: $reply", false)
                   }
               }


           }
       }

   }

   // Añade un TextView por mensaje
   private fun addMessageToChat(container: LinearLayout, message: String, isUser: Boolean) {
       val textView = TextView(this)
       textView.text = message
       textView.textSize = 16f
       textView.setPadding(20, 15, 20, 15)
       textView.setTextColor(resources.getColor(android.R.color.black))

       if (isUser) {
           textView.setBackgroundResource(R.drawable.puntanubechat)
       } else {
           textView.setBackgroundResource(R.drawable.puntanubechat)
       }

       val params = LinearLayout.LayoutParams(
           LinearLayout.LayoutParams.WRAP_CONTENT,
           LinearLayout.LayoutParams.WRAP_CONTENT
       )

       params.setMargins(15, 10, 15, 10)
       if (isUser) params.gravity = android.view.Gravity.END else params.gravity = android.view.Gravity.START
       textView.layoutParams = params

       container.addView(textView)

       // Auto scroll hacia el final
       val scrollView = findViewById<android.widget.ScrollView>(R.id.scrollChat)
       scrollView.post { scrollView.fullScroll(android.view.View.FOCUS_DOWN) }
   }



    }

*/


package com.example.proyecto_final
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import android.graphics.Typeface
import android.graphics.drawable.AnimationDrawable
import android.view.Gravity
import android.widget.ImageView
import android.view.View

class Act7ChatTutor : AppCompatActivity() {
    private val apiKey = BuildConfig.OPENAI_API_KEY
    private var openAIClient = OpenAIClient(BuildConfig.OPENAI_API_KEY)
    //para la vista dentro del chat
    private lateinit var chatContainer: LinearLayout
    private lateinit var scrollView: ScrollView
    private var typingAnimationView: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.pantallachat)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chatTutor)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //se obtiene el nombre del usuario
        val sharedPref=getSharedPreferences("MiAppPrefs",Context.MODE_PRIVATE)
        //se obtiene el nombre
        val nombreUsuario=sharedPref.getString("user_name","Usuario")
        val txtNombre=findViewById<TextView>(R.id.txtNombreUser)

        //se pone el nombre del usuario
        txtNombre.text="Benvenid@ "+nombreUsuario

        val editTextMessage = findViewById<EditText>(R.id.editTextMessage)
        val sendButton = findViewById<ImageButton>(R.id.imageButtonSend)
        chatContainer = findViewById<LinearLayout>(R.id.chatAreaContainer)
        scrollView = findViewById<ScrollView>(R.id.scrollChat)
        val nombreUsario=findViewById<TextView>(R.id.txtNombreUser)


        sendButton.setOnClickListener {
            val userMessage = editTextMessage.text.toString().trim()

            if (userMessage.isNotEmpty()) {
                addMessageToChat(chatContainer, userMessage, true)
                editTextMessage.text.clear()

                //ANIMACION
                typingAnimationView=showTypingAnimation(chatContainer)

                openAIClient.sendMessage(userMessage) { response ->
                    runOnUiThread {
                        //para la animación
                        stopAnimation(chatContainer,typingAnimationView)

                        val reply = response ?: "Error o sin respuesta"
                        addMessageToChat(chatContainer, reply, false)
                        scrollView.post { scrollView.fullScroll(android.view.View.FOCUS_DOWN) }
                    }
                }
            }
        }
    }


    //Visualización del mensaje

    private fun addMessageToChat(container: LinearLayout, message: String, isUser: Boolean) {
        // detección de errores y contenido
        val isError = message.contains("Error de red") || message.contains("Error de la IA") || message.contains("Error o sin respuesta")

        // detectar mensajes
        if (isUser || isError) {

            if (isUser) {
                // Usuario: siempre simple
                val userTextView = createStyledTextView(message, isUser)
                container.addView(userTextView)
            } else {
                //textview a interfaz con un lininear layout
                val errorBubbleContainer = LinearLayout(this)
                errorBubbleContainer.orientation = LinearLayout.HORIZONTAL
                errorBubbleContainer.setPadding(20, 15, 20, 15)
                // el drawable de la burbuja de error/tutor
                errorBubbleContainer.setBackgroundResource(R.drawable.nubechatchat)

                // agregar la imagen de error
                val errorImg = createErrorIcon()
                errorBubbleContainer.addView(errorImg)

                // crear el TextView del error
                val errorTextView = createStyledTextView(message, isUser)
                errorTextView.setPadding(0, 0, 0, 0) // quitar padding extra del TextView
                errorTextView.background = null // quitar fondo del TextView
                //se agrega a la vista
                errorBubbleContainer.addView(errorTextView)

                //aplicar los parámetros de layout y agregar al chat principal
                val bubbleParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                bubbleParams.setMargins(15, 10, 15, 10)
                bubbleParams.gravity = Gravity.START

                container.addView(errorBubbleContainer, bubbleParams)
            }

        } else {
            //TUTOR CON CÓDIGO/HTML (División por <pre>) - codigo

            val parts = message.split("<pre>", "</pre>")

            for (i in parts.indices) {
                val part = parts[i].trim()
                if (part.isEmpty()) continue

                if (i % 2 == 1) {
                    // PARTE CÓDIGO: Se agrega directamente al contenedor principal
                    val code = part.replace("<code>", "").replace("</code>", "").trim()
                    val codeTextView = createCodeTextView(code)
                    container.addView(codeTextView)
                } else {
                    // PARTE TEXTO/HTML NORMAL
                    val textToDisplay = HtmlCompat.fromHtml(part, HtmlCompat.FROM_HTML_MODE_COMPACT)
                    val textTextView = createStyledTextView(textToDisplay, isUser)
                    container.addView(textTextView)
                }
            }
        }

        // auto scroll
        val scrollView = findViewById<ScrollView>(R.id.scrollChat)
        scrollView.post { scrollView.fullScroll(android.view.View.FOCUS_DOWN) }
    }

    //textView con el estilo de burbuja  (usuario o tutor).
    private fun createStyledTextView(text: CharSequence, isUser: Boolean): TextView {

        //text view
        val textView = TextView(this)
        textView.text = text
        textView.textSize = 16f
        textView.setPadding(20, 15, 20, 15)
        textView.setTextColor(resources.getColor(android.R.color.black))
        //?
        val bubble = if (isUser) R.drawable.nubechat else R.drawable.nubechatchat
        textView.setBackgroundResource(bubble)
        //fuente
        val typeface = ResourcesCompat.getFont(this, R.font.aoboshi_one)
        textView.typeface = typeface // Fuente para texto normal
        //layout
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(15, 10, 15, 10)
        params.gravity = if (isUser) Gravity.END else Gravity.START
        textView.layoutParams = params
        return textView
    }

    //textView específico para el código (fondo oscuro, fuente monoespaciada, indentación). */
    private fun createCodeTextView(code: String): TextView {
        val textView = TextView(this)

        // Reemplaza \n codificado (si lo envía la IA) y asegura saltos de línea y trim.
        // El uso de Typeface.MONOSPACE es clave para que los espacios de indentación funcionen.
        val indentedCode = code.replace("\\n", "\n").trim()

        textView.text = indentedCode

        textView.textSize = 14f
        textView.setPadding(20, 20, 20, 20)
        textView.setTextColor(resources.getColor(android.R.color.white))
        textView.setBackgroundResource(R.drawable.nubenubechatnegra);

        // Usa una fuente monoespaciada para la indentación
        textView.typeface = Typeface.MONOSPACE

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(15, 20, 15, 20)
        params.gravity = Gravity.START
        textView.layoutParams = params
        return textView
    }

    //funcion para la vista de errores :
    private fun createErrorIcon(): ImageView {
        val imageView = ImageView(this)
        // ícono de advertencia
        imageView.setImageResource(R.drawable.warning)

        // el tamaño de la imagen (ej. 32dp)
        val sizeInPx = (50 * resources.displayMetrics.density).toInt()

        val params = LinearLayout.LayoutParams(sizeInPx, sizeInPx)
        // agrega margen a la derecha para separarlo del texto
        params.setMargins(0, 0, 10, 0)
        // alinea verticalmente con el centro del texto
        params.gravity = Gravity.CENTER_VERTICAL

        imageView.layoutParams = params
        return imageView
    }

    private fun showTypingAnimation(container: LinearLayout): LinearLayout {
        // crear el ImageView con la animación
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.animationchat)

        // crear la BURBUJA (LinearLayout horizontal)
        val typingBubbleContainer = LinearLayout(this)
        typingBubbleContainer.orientation = LinearLayout.HORIZONTAL

        // cplicar estilo de burbuja al contenedor
        //typingBubbleContainer.setBackgroundResource(R.drawable.nubechatchat)
        typingBubbleContainer.setPadding(15, 10, 15, 10)

        // ajustar tamaño del ImageView (la animación de los puntos)
        val width = (80 * resources.displayMetrics.density).toInt()
        val height = (100 * resources.displayMetrics.density).toInt()

        val imageParams = LinearLayout.LayoutParams(width, height)
        imageParams.gravity = Gravity.CENTER_VERTICAL

        // agregar el ImageView a la burbuja
        imageView.layoutParams = imageParams
        typingBubbleContainer.addView(imageView)


        // AGREGAR EL TEXTO "PENSANDO"
        val thinkingText = TextView(this)

        //obtener el Typeface de la fuente
        val customTypeface = ResourcesCompat.getFont(this, R.font.aoboshi_one)
        thinkingText.typeface = customTypeface

        thinkingText.text = "Pensando..."

        thinkingText.textSize = 30f

        thinkingText.setTextColor(resources.getColor(android.R.color.black)) // O el color que desees

        // asegurarse que el texto esté centrado verticalmente y tenga un poco de margen
        val textParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        textParams.gravity = Gravity.CENTER_VERTICAL
        textParams.marginStart = (8 * resources.displayMetrics.density).toInt() // Margen entre animación y texto
        thinkingText.layoutParams = textParams

        // agregar el TextView a la burbuja
        typingBubbleContainer.addView(thinkingText)

        // iniciar la animación
        val animation = imageView.drawable as? AnimationDrawable
        animation?.start()

        // aplicar parámetros de layout y agregar la burbuja al chat principal
        val bubbleParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        bubbleParams.setMargins(15, 10, 15, 10)
        bubbleParams.gravity = Gravity.START

        container.addView(typingBubbleContainer, bubbleParams)

        // Auto scroll
        val scrollView = findViewById<ScrollView>(R.id.scrollChat)
        scrollView.post { scrollView.fullScroll(View.FOCUS_DOWN) }

        // Retornamos el CONTENEDOR COMPLETO de la burbuja
        return typingBubbleContainer
    }
    private fun stopAnimation(container: LinearLayout, animationView: LinearLayout?) {
        animationView?.let { bubble ->
            // detener la animación buscando el ImageView dentro de la burbuja
            val imageView = bubble.getChildAt(0) as? ImageView
            val animation = imageView?.drawable as? AnimationDrawable
            animation?.stop()

            // eliminar la burbuja completa
            container.removeView(bubble)
        }
    }



}