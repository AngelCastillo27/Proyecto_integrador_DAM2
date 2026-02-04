package com.example.inventario_pi_v1.activities.departamentos

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.inventario_pi_v1.R
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class ReportesActivity : AppCompatActivity() {

    private lateinit var contenedor: LinearLayout
    private lateinit var usuarioActual: String
    private lateinit var rolActual: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes)

        usuarioActual = intent.getStringExtra("USUARIO") ?: ""
        rolActual = intent.getStringExtra("ROL") ?: ""

        contenedor = findViewById(R.id.contenedorReportes)
        findViewById<Button>(R.id.btnRetorno_ini_gener).setOnClickListener { finish() }

        // 1. Cargamos la lista de reportes
        cargarReportes()

        // 2. Agregamos el botón de IA (solo si es ADMIN)
        configurarBotonIA()
    }

    private fun configurarBotonIA() {
        if (rolActual.uppercase() == "ADMIN") {
            val btnAI = Button(this)
            btnAI.text = "📊 ANALIZAR TENDENCIAS CON IA"
            btnAI.setBackgroundColor(Color.parseColor("#1A237E"))
            btnAI.setTextColor(Color.WHITE)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 30)
            btnAI.layoutParams = params

            btnAI.setOnClickListener {
                val intent = Intent(this, AnalisisIAActivity::class.java)
                startActivity(intent)
            }

            // Lo añadimos al principio del contenedor (índice 0)
            contenedor.addView(btnAI, 0)
        }
    }

    private fun cargarReportes() {
        Thread {
            try {
                val url = URL("http://10.0.2.2/inventario/obtener_reportes.php")
                val conexion = url.openConnection() as HttpURLConnection
                conexion.requestMethod = "POST"
                conexion.doOutput = true

                val data = "usuario=${URLEncoder.encode(usuarioActual, "UTF-8")}&rol=${URLEncoder.encode(rolActual, "UTF-8")}"
                conexion.outputStream.write(data.toByteArray())

                val respuesta = conexion.inputStream.bufferedReader().readText().trim()

                runOnUiThread {
                    // Al recargar, guardamos el botón de IA si existe para no borrarlo
                    val vistaIA = if (rolActual.uppercase() == "ADMIN") contenedor.getChildAt(0) else null

                    contenedor.removeAllViews()

                    // Re-insertamos el botón de IA si existía
                    if (vistaIA is Button) {
                        contenedor.addView(vistaIA)
                    } else if (rolActual.uppercase() == "ADMIN") {
                        // Si por alguna razón se perdió, lo volvemos a configurar
                        configurarBotonIA()
                    }

                    if (respuesta.isNotEmpty() && !respuesta.startsWith("Error")) {
                        val filas = respuesta.split(";")
                        for (fila in filas) {
                            val partes = fila.split("|")
                            if (partes.size >= 4) {
                                agregarItemLista(partes[0], partes[1], partes[2], partes[3])
                            }
                        }
                    } else {
                        Toast.makeText(this, "No hay inventarios realizados", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread { Toast.makeText(this, "Error de conexión", Toast.LENGTH_LONG).show() }
            }
        }.start()
    }

    private fun agregarItemLista(idInv: String, user: String, turno: String, fecha: String) {
        val itemPrincipal = LinearLayout(this)
        itemPrincipal.orientation = LinearLayout.VERTICAL
        val paramsItem = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        paramsItem.setMargins(0, 0, 0, 25)
        itemPrincipal.layoutParams = paramsItem
        itemPrincipal.setBackgroundResource(android.R.drawable.dialog_holo_light_frame)

        val cabecera = LinearLayout(this)
        cabecera.orientation = LinearLayout.HORIZONTAL
        cabecera.setPadding(35, 35, 35, 35)
        cabecera.gravity = Gravity.CENTER_VERTICAL

        val tvInfo = TextView(this)
        val fechaCorta = if(fecha.length > 10) fecha.substring(0, 10) else fecha
        tvInfo.text = "$user - $turno - $fechaCorta"
        tvInfo.textSize = 16f
        tvInfo.typeface = Typeface.DEFAULT_BOLD
        tvInfo.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        tvInfo.setTextColor(Color.BLACK)

        cabecera.addView(tvInfo)

        if (rolActual.uppercase() == "ADMIN") {
            val btnEliminar = ImageButton(this)
            btnEliminar.setImageResource(android.R.drawable.ic_menu_delete)
            btnEliminar.setBackgroundColor(Color.TRANSPARENT)
            btnEliminar.setOnClickListener { confirmarEliminacion(idInv) }
            cabecera.addView(btnEliminar)
        }

        val tvDetalles = TextView(this)
        tvDetalles.setPadding(50, 0, 50, 35)
        tvDetalles.visibility = View.GONE
        tvDetalles.setTextColor(Color.DKGRAY)
        tvDetalles.textSize = 14f

        cabecera.setOnClickListener {
            if (tvDetalles.visibility == View.GONE) {
                if (tvDetalles.text.isEmpty()) {
                    cargarDetallesProductos(idInv, tvDetalles)
                }
                tvDetalles.visibility = View.VISIBLE
                itemPrincipal.setBackgroundColor(Color.parseColor("#E3F2FD"))
            } else {
                tvDetalles.visibility = View.GONE
                itemPrincipal.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        itemPrincipal.addView(cabecera)
        itemPrincipal.addView(tvDetalles)
        contenedor.addView(itemPrincipal)
    }

    private fun cargarDetallesProductos(idInv: String, tvDetalles: TextView) {
        Thread {
            try {
                val url = URL("http://10.0.2.2/inventario/obtener_detalles_inventario.php")
                val conexion = url.openConnection() as HttpURLConnection
                conexion.requestMethod = "POST"
                conexion.doOutput = true
                val data = "inventario_id=$idInv"
                conexion.outputStream.write(data.toByteArray())

                val respuesta = conexion.inputStream.bufferedReader().readText().trim()

                runOnUiThread {
                    tvDetalles.text = if (respuesta.isEmpty()) "Sin productos registrados" else "PRODUCTOS:\n$respuesta"
                }
            } catch (e: Exception) {
                runOnUiThread { tvDetalles.text = "Error al cargar detalles" }
            }
        }.start()
    }

    private fun confirmarEliminacion(idInv: String) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Inventario")
            .setMessage("¿Estás seguro que deseas eliminar permanentemente este inventario?")
            .setPositiveButton("SÍ, ELIMINAR") { _, _ -> eliminarInventario(idInv) }
            .setNegativeButton("CANCELAR", null)
            .show()
    }

    private fun eliminarInventario(idInv: String) {
        Thread {
            try {
                val url = URL("http://10.0.2.2/inventario/eliminar_inventario.php")
                val conexion = url.openConnection() as HttpURLConnection
                conexion.requestMethod = "POST"
                conexion.doOutput = true
                val data = "inventario_id=$idInv"
                conexion.outputStream.write(data.toByteArray())
                val respuesta = conexion.inputStream.bufferedReader().readText().trim()

                runOnUiThread {
                    if (respuesta == "OK") {
                        Toast.makeText(this, "Inventario eliminado", Toast.LENGTH_SHORT).show()
                        cargarReportes()
                    } else {
                        Toast.makeText(this, "Error: $respuesta", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread { Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show() }
            }
        }.start()
    }
}