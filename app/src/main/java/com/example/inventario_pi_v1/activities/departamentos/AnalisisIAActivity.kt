package com.example.inventario_pi_v1.activities.departamentos

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.inventario_pi_v1.R

class AnalisisIAActivity : AppCompatActivity() {

    private lateinit var tvResultado: TextView
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analisis_ia)

        tvResultado = findViewById(R.id.tvResultadoIA)
        progress = findViewById(R.id.progressIA)

        findViewById<Button>(R.id.btnCerrarIA).setOnClickListener {
            finish()
        }

        llamarServidorLocalIA()
    }

    private fun llamarServidorLocalIA() {
        progress.visibility = View.VISIBLE
        tvResultado.text = "Contactando con la IA local..."

        val idsSeleccionadas = intent.getStringExtra("IDS_SELECCIONADAS") ?: ""

        if (idsSeleccionadas.isEmpty()) {
            progress.visibility = View.GONE
            tvResultado.text = "No se seleccionaron inventarios para analizar."
            return
        }

        // Llamamos al PHP que se encargará de hablar con LM Studio
        val url = "http://10.0.2.2/inventario/analizar_inventarios_ia.php"
        //CORREGIDO

        val request = object : StringRequest(
            Request.Method.POST,
            url,
            { response ->
                progress.visibility = View.GONE
                if (response.isNotEmpty() && !response.startsWith("Error")) {
                    // Mostramos directamente lo que nos dice nuestra IA local
                    tvResultado.text = response.trim()
                } else {
                    tvResultado.text = "No se pudo generar el análisis. Respuesta del servidor: \n$response"
                }
            },
            {
                progress.visibility = View.GONE
                tvResultado.text = "Error crítico al conectar con el servidor local (XAMPP)."
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                // Enviamos los IDs de los inventarios seleccionados al PHP
                return hashMapOf("ids" to idsSeleccionadas)
            }
        }

        // Añadimos más tiempo de espera, ya que la IA local puede tardar
        request.retryPolicy = com.android.volley.DefaultRetryPolicy(
            120000, // 2 minutos
            com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        Volley.newRequestQueue(this).add(request)
    }
}
