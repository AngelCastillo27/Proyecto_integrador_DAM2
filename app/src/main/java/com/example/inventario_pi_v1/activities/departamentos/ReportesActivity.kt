package com.example.inventario_pi_v1.activities.departamentos

import android.app.AlertDialog
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inventario_pi_v1.R
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class ReportesActivity : AppCompatActivity() {

    private lateinit var contenedor: LinearLayout
    private lateinit var usuario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes)

        contenedor = findViewById(R.id.contenedorReportes)
        usuario = intent.getStringExtra("USUARIO") ?: ""

        cargarReportes()
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
                    contenedor.removeAllViews()
                    if (respuesta.startsWith("OK|")) {
                        val items = respuesta.removePrefix("OK|").split("||")
                        for (item in items) {
                            if (item.isNotBlank()) crearCardReporte(item)
                        }
                    } else {
                        Toast.makeText(this, respuesta, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error al cargar reportes", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    private fun crearCardReporte(datos: String) {
        // Datos esperados: id_inventario|turno|fecha_hora|detalle
        val partes = datos.split("|")
        if (partes.size < 4) return

        val idInventario = partes[0]
        val info = "${partes[1]} - ${partes[2]}"

        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(16, 16, 16, 16)
        card.setBackgroundResource(R.drawable.card_background)

        val tvInfo = TextView(this)
        tvInfo.text = info

        val tvDetalle = TextView(this)
        tvDetalle.text = partes[3]

        val btnEliminar = Button(this)
        btnEliminar.text = "Eliminar inventario"
        btnEliminar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Eliminar inventario")
                .setMessage("¿Seguro que deseas eliminar este inventario?")
                .setPositiveButton("Sí") { _, _ -> eliminarInventario(idInventario) }
                .setNegativeButton("No", null)
                .show()
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
                    cargarReportes()
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error al eliminar inventario", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }
}
