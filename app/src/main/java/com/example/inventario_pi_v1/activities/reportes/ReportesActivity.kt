package com.example.inventario_pi_v1.activities.reportes

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inventario_pi_v1.R
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class ReportesActivity : AppCompatActivity() {

    private lateinit var contenedor: LinearLayout
    private lateinit var usuario: String
    private var rol: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes)

        contenedor = findViewById(R.id.contenedorReportes)

        // Recuperar datos del Intent
        usuario = intent.getStringExtra("USUARIO") ?: ""
        rol = intent.getStringExtra("ROL") ?: ""

        // Configurar botón de IA solo si es ADMIN
        configurarBotonIA()

        cargarReportes()
    }

    private fun configurarBotonIA() {
        if (rol == "ADMIN") {
            val btnAnalizarIA = Button(this).apply {
                text = "Analizar con IA (Admin)"
                setOnClickListener {
                    // Asegúrate de que AnalisisIAActivity esté declarada en el Manifest
                    startActivity(Intent(this@ReportesActivity, AnalisisIAActivity::class.java))
                }
            }
            // Se añade en la posición 0 para que aparezca al principio de la lista
            contenedor.addView(btnAnalizarIA, 0)
        }
    }

    private fun cargarReportes() {
        Thread {
            try {
                val url = URL("http://10.0.2.2/inventario/listar_inventarios.php")
                val conexion = url.openConnection() as HttpURLConnection
                conexion.requestMethod = "POST"
                conexion.doOutput = true

                val data = "usuario=${URLEncoder.encode(usuario, "UTF-8")}"
                conexion.outputStream.write(data.toByteArray())

                val respuesta = conexion.inputStream.bufferedReader().readText()

                runOnUiThread {
                    // Limpiamos pero respetamos el botón de IA si existe
                    actualizarContenedor(respuesta)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error al cargar reportes", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    private fun actualizarContenedor(respuesta: String) {
        // Guardamos el botón de IA si es que está presente para no borrarlo al refrescar
        val vistaIA = if (rol == "ADMIN") contenedor.getChildAt(0) else null

        contenedor.removeAllViews()

        // Re-insertamos el botón de IA si existía
        vistaIA?.let { contenedor.addView(it) }

        if (respuesta.startsWith("OK|")) {
            val items = respuesta.removePrefix("OK|").split("||")
            for (item in items) {
                if (item.isNotBlank()) crearCardReporte(item)
            }
        } else {
            Toast.makeText(this, respuesta, Toast.LENGTH_LONG).show()
        }
    }

    private fun crearCardReporte(datos: String) {
        val partes = datos.split("|")
        if (partes.size < 4) return

        val idInventario = partes[0]
        val info = "${partes[1]} - ${partes[2]}"

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            // Asegúrate de tener este drawable o cambiarlo por un color/borde
            setBackgroundResource(R.drawable.card_background)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 8, 0, 8)
            layoutParams = params
        }

        val tvInfo = TextView(this).apply {
            text = info
            textSize = 16f
        }

        val tvDetalle = TextView(this).apply {
            text = partes[3]
        }

        val btnEliminar = Button(this).apply {
            text = "Eliminar inventario"
            setOnClickListener {
                AlertDialog.Builder(this@ReportesActivity)
                    .setTitle("Eliminar inventario")
                    .setMessage("¿Seguro que deseas eliminar este inventario?")
                    .setPositiveButton("Sí") { _, _ -> eliminarInventario(idInventario) }
                    .setNegativeButton("No", null)
                    .show()
            }
        }

        card.addView(tvInfo)
        card.addView(tvDetalle)
        card.addView(btnEliminar)

        contenedor.addView(card)
    }

    private fun eliminarInventario(idInventario: String) {
        Thread {
            try {
                val url = URL("http://10.0.2.2/inventario/eliminar_inventario.php")
                val conexion = url.openConnection() as HttpURLConnection
                conexion.requestMethod = "POST"
                conexion.doOutput = true

                val data = "id=${URLEncoder.encode(idInventario, "UTF-8")}"
                conexion.outputStream.write(data.toByteArray())

                val respuesta = conexion.inputStream.bufferedReader().readText()

                runOnUiThread {
                    Toast.makeText(this, respuesta, Toast.LENGTH_LONG).show()
                    cargarReportes() // Refrescar lista
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error al eliminar", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }
}