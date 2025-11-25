
package com.example.proyecto_final
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import okhttp3.RequestBody.Companion.toRequestBody
import android.util.Log
import java.lang.Exception

class OpenAIClient(private val apiKey: String) {

    private val url = BuildConfig.OPENAI_URL

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)  // tiempo para conectar
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)    // tiempo para enviar el cuerpo
        .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)    // tiempo para leer la respuesta
        .build()



    fun sendMessage(prompt: String, callback: (String?) -> Unit) {

        val json = JSONObject().apply {
            put("model", "gpt-4o-mini")
            val messages = org.json.JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", """
                Eres un tutor amable que enseña temas educativos de forma clara y completa.No involucras otros temas que NO SEAN ACADEMICOS.
                **REGLAS DE FORMATO ESTRICTAS:**
                1.  El título principal va **UNA SOLA VEZ** usando **<h1>** (Ej: <h1>Título Principal</h1>).
                2.  Usa formato **HTML BÁSICO**. Usa **<p>** para párrafos, **<b>** para negritas y **<br>** para saltos de línea.
                3.  Para listas de pasos: usa **<ol> y <li>**. **Asegúrate de que cada <li> termine con una etiqueta <br> para un salto de línea visual grande.**
                4. Para bloques de código: usa **<pre> y <code>**. Inserta TRES saltos de línea (<br><br><br>) antes y después de </pre>. Dentro de <pre>, el código debe estar **correctamente indentado** usando **espacios** (no tabulaciones ni la secuencia \n) y con **saltos de línea reales** (no \n codificado). No uses la etiqueta <p> dentro de <pre>.
                5.  Fórmulas matemáticas: texto plano (Ej: "a^2 + b^2 = c^2"). No uses LaTeX.   
                Si la respuesta es larga, divídela naturalmente en partes completas sin cortar una idea o fórmula.
                
                Al final de cada explicación, si consideras que el tema puede continuar, pregunta: 
                "¿Deseas que siga explicando este tema o pasamos a otro?".
                Si el usuario te responde con un "si" continua con la explicación, dale continuidad a la conversación . No involucres temas que sean diferentes hasta que el mismo usuario te pregunte por otro tema, de lo contrario continua con el tema que te ha preguntado.
                
                IMPORTANTE:
                No termines una explicación a la mitad de una frase o fórmula. 
                Siempre concluye una idea antes de detenerte.
            """.trimIndent())
                })

                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            }
            put("messages", messages)
            put("max_tokens", 700)
            put("temperature", 0.7)
            put("presence_penalty", 0.0)
            put("frequency_penalty", 0.3)
        }




        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            //.url("https://api.openai.com/v1/chat/completions")
            .url(url)
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer $apiKey") // importante
            .post(body)
            .build()


        Log.d("OpenAIClient", "Enviando solicitud a OpenAI...")
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("OpenAIClient", "Error: ${e.message}", e)
                callback("Error de red. ${e.message}")

            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        val errorBody = it.body?.string()

                        //errror especifico
                        val errorMessage=try{
                            JSONObject(errorBody!!).getJSONObject("error").getString("message")
                        }catch (e: Exception){
                            //se manda el error generico
                            "Error HTTP ${it.code} :${it.message}"
                        }

                        callback("Error de la IA . $errorMessage")
                        return
                    } else {
                        val bodyString = it.body?.string()
                        val jsonResponse = JSONObject(bodyString)
                        val content = jsonResponse
                            .getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content")
                        callback(content)
                    }
                }
            }
        })
    }

}
