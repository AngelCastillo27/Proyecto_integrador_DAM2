package com.example.inventario_pi_v1.activities.reportes

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.inventario_pi_v1.R
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AnalisisIAActivity : AppCompatActivity() {

    private lateinit var tvResultado: TextView
    private lateinit var progress: ProgressBar
    
    // AQUÍ ESTÁ TU API KEY
    private val apiKey = "AIzaSyAlSPvXIL27ThQ1ES1EY4NrQZr8mE2QP4c"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analisis_ia)

        tvResultado = findViewById(R.id.tvResultadoIA)
        progress = findViewById(R.id.progressIA)
        findViewById<Button>(R.id.btnCerrarIA).setOnClickListener { finish() }

        obtenerDatosYConsultarIA()
    }

    private fun obtenerDatosYConsultarIA() {
        progress.visibility = View.VISIBLE
        tvResultado.text = "Obteniendo datos de inventarios locales..."

        val url = "http://10.0.2.2/inventario/obtener_datos_comparativa.php"

        val queue = Volley.newRequestQueue(this)
        val request = StringRequest(Request.Method.GET, url, { response ->
            if (response.isNullOrEmpty()) {
                tvResultado.text = "No hay datos suficientes para analizar."
                progress.visibility = View.GONE
            } else {
                consultarGeminiAPI(response)
            }
        }, {
            tvResultado.text = "Error al obtener datos del servidor local (XAMPP)."
            progress.visibility = View.GONE
        })
        queue.add(request)
    }

    private fun consultarGeminiAPI(datosInventario: String) {
        tvResultado.text = "La IA de Google está analizando tus inventarios..."
        
        // Configuramos el modelo de Gemini
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey
        )

        val prompt = """
            Actúa como un experto en logística y analítica de ventas para una tienda en Madrid, España.
            Analiza los siguientes datos de los últimos 3 inventarios:
            
            $datosInventario
            
            REGLAS:
            1. NORMALIZACIÓN: Si detectas nombres similares (ej: 'Galleta Choco' y 'Galleta Chocolate'), identifícalos como el mismo producto.
            2. COMPARATIVA: Calcula la diferencia de stock/ventas entre los registros.
            3. MERCADO: Busca o estima datos de consumo actuales en Madrid para estos tipos de productos.
            
            RESPONDE CON ESTA ESTRUCTURA EXACTA:
            
            ANÁLISIS DE INVENTARIO:
            (Aquí tu análisis de diferencias y normalización de nombres)
            
            DATOS DE MERCADO (MADRID):
            (Aquí tendencias actuales de consumo per cápita en Madrid para estos productos)
            
            RECOMENDACIÓN:
            (Aquí qué stock conviene tener y qué producto se vende menos)
            
            Responde de forma profesional y clara en español.
        """.trimIndent()

        // Ejecutamos la llamada real a la IA
        MainScope().launch {
            try {
                val response = generativeModel.generateContent(prompt)
                progress.visibility = View.GONE
                tvResultado.text = response.text ?: "La IA no pudo generar una respuesta en este momento."
            } catch (e: Exception) {
                progress.visibility = View.GONE
                tvResultado.text = "Error al conectar con la IA: ${e.message}"
            }
        }
    }
}
