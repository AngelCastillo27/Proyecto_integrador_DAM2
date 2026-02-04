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

        // Llamada directa al backend con IA
        llamarAnalisisIA()
    }

    private fun llamarAnalisisIA() {
        progress.visibility = View.VISIBLE
        tvResultado.text = "La IA está analizando los inventarios..."

        val url = "http://10.0.2.2/inventario/analizar_inventarios_ia.php"

        val request = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                progress.visibility = View.GONE
                tvResultado.text = if (response.isNotEmpty()) {
                    response
                } else {
                    "La IA no devolvió resultados."
                }
            },
            {
                progress.visibility = View.GONE
                tvResultado.text = "Error al conectar con el servidor."
            }
        )
        request.retryPolicy = com.android.volley.DefaultRetryPolicy(
            //6 minutos
            360000,
            0,
            com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        Volley.newRequestQueue(this).add(request)

    }
}
