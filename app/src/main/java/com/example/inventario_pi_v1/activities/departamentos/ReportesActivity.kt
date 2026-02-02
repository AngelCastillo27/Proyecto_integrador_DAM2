package com.example.inventario_pi_v1.activities.departamentos

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
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

        cargarReportes()
    }

    private fun cargarReportes() {
        contenedor.removeAllViews()
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
        val layoutItem = LinearLayout(this)
        layoutItem.orientation = LinearLayout.HORIZONTAL
        layoutItem.setPadding(30, 30, 30, 30)
        
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(0, 0, 0, 20)
        layoutItem.layoutParams = params
        layoutItem.setBackgroundResource(android.R.drawable.dialog_holo_light_frame)
        layoutItem.gravity = Gravity.CENTER_VERTICAL

        // Texto informativo: USUARIO - TURNO - DIA
        val tvInfo = TextView(this)
        val fechaCorta = if(fecha.length > 10) fecha.substring(0, 10) else fecha
        tvInfo.text = "$user - $turno - $fechaCorta"
        tvInfo.textSize = 16f // Corregido: 16f en lugar de 16sp
        tvInfo.typeface = Typeface.DEFAULT_BOLD
        tvInfo.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        tvInfo.setTextColor(Color.BLACK)

        layoutItem.addView(tvInfo)

        // Botón Ver (Para todos)
        val btnVer = ImageButton(this)
        btnVer.setImageResource(android.R.drawable.ic_menu_view)
        btnVer.setBackgroundColor(Color.TRANSPARENT)
        btnVer.setOnClickListener {
            Toast.makeText(this, "Detalles del inventario $idInv", Toast.LENGTH_SHORT).show()
            // Aquí podrías implementar una vista de detalle más adelante
        }
        layoutItem.addView(btnVer)

        // Botón Eliminar (Solo para ADMIN)
        if (rolActual.uppercase() == "ADMIN") {
            val btnEliminar = ImageButton(this)
            btnEliminar.setImageResource(android.R.drawable.ic_menu_delete)
            btnEliminar.setBackgroundColor(Color.TRANSPARENT)
            btnEliminar.setOnClickListener {
                confirmarEliminacion(idInv)
            }
            layoutItem.addView(btnEliminar)
        }

        contenedor.addView(layoutItem)
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
                        Toast.makeText(this, "Inventario eliminado correctamente", Toast.LENGTH_SHORT).show()
                        cargarReportes() // Recargar la lista automáticamente
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
