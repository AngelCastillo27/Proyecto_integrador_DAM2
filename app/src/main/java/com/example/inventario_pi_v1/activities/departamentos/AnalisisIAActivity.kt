package com.example.inventario_pi_v1.activities.departamentos

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
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
        tvResultado.text = "Obteniendo datos de inventarios..."

        val url = "http://10.0.2.2/inventario/obtener_datos_comparativa.php"
        
        val request = StringRequest(Request.Method.GET, url, { response ->
            if (response.isNotEmpty()) {
                llamarAGemini(response)
            } else {
                progress.visibility = View.GONE
                tvResultado.text = "No hay suficientes datos para comparar."
            }
        }, {
            progress.visibility = View.GONE
            tvResultado.text = "Error al conectar con el servidor local."
        })
        
        Volley.newRequestQueue(this).add(request)
    }

    private fun llamarAGemini(datosInventarios: String) {
        tvResultado.text = "La IA está analizando los datos..."
        
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey
        )

        val prompt = """
            Actúa como un experto en analítica de ventas y logística. 
            Te proporciono los datos de los últimos 3 inventarios:
            $datosInventarios

            Tareas:
            1. Compara los inventarios. Si detectas nombres similares (ej: 'Galleta Choco' y 'Galleta Chocolate'), identifícalos como el mismo producto.
            2. Indica la diferencia vendida entre los registros.
            3. Identifica el producto menos vendido.
            4. Basándote en tendencias de consumo en Madrid (España), indica si el consumo per cápita de estos productos es alto o bajo.
            5. Recomienda cuánto stock conviene tener de acuerdo a la velocidad de venta observada.

            Responde en español de forma estructurada, profesional y con consejos accionables para el administrador.
        """.trimIndent()

        MainScope().launch {
            try {
                val response = generativeModel.generateContent(prompt)
                progress.visibility = View.GONE
                tvResultado.text = response.text ?: "La IA no pudo generar una respuesta."
            } catch (e: Exception) {
                progress.visibility = View.GONE
                tvResultado.text = "Error de IA: ${e.message}"
                Toast.makeText(this@AnalisisIAActivity, "Error en Gemini", Toast.LENGTH_LONG).show()
            }
        }
    }
}
