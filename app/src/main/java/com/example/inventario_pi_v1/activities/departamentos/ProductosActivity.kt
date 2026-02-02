package com.example.inventario_pi_v1.activities.departamentos

import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.inventario_pi_v1.R
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class ProductosActivity : AppCompatActivity() {

    private lateinit var contenedor: LinearLayout
    private lateinit var usuario: String
    private lateinit var departamento: String
    private lateinit var turno: String // ✅ Variable para almacenar el turno

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)

        // 1. Capturamos los datos del Intent, incluyendo el TURNO
        usuario = intent.getStringExtra("USUARIO") ?: ""
        departamento = intent.getStringExtra("DEPARTAMENTO") ?: ""
        turno = intent.getStringExtra("TURNO") ?: "M" // ✅ Recibimos el turno (por defecto "M")

        findViewById<TextView>(R.id.tvTitulo).text = departamento
        contenedor = findViewById(R.id.contenedorProductos)

        // Botones
        findViewById<Button>(R.id.btnAgregar).setOnClickListener { agregarFila("", "") }
        findViewById<Button>(R.id.btnGuardar).setOnClickListener { guardarProductos() }
        findViewById<Button>(R.id.btnRetorno_depgen).setOnClickListener { finish() }

        cargarProductos()
    }

    private fun agregarFila(nombre: String, cantidad: String) {
        val fila = LinearLayout(this)
        fila.orientation = LinearLayout.HORIZONTAL
        fila.setPadding(8, 8, 8, 8)

        val etNombre = EditText(this)
        etNombre.hint = "Producto"
        etNombre.setText(nombre)
        etNombre.layoutParams =
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)

        val etCantidad = EditText(this)
        etCantidad.hint = "Cant."
        etCantidad.inputType = InputType.TYPE_CLASS_NUMBER
        etCantidad.setText(cantidad)
        etCantidad.layoutParams =
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

        val btnEliminar = Button(this)
        btnEliminar.text = "❌"
        btnEliminar.setOnClickListener { contenedor.removeView(fila) }

        fila.addView(etNombre)
        fila.addView(etCantidad)
        fila.addView(btnEliminar)

        contenedor.addView(fila, 0)
    }

    private fun cargarProductos() {
        Thread {
            try {
                val url = URL("http://10.0.2.2/inventario/listar_productos.php")
                val conexion = url.openConnection() as HttpURLConnection
                conexion.requestMethod = "POST"
                conexion.doOutput = true

                // ✅ También enviamos el turno al listar para filtrar correctamente si es necesario
                val data =
                    "usuario=${URLEncoder.encode(usuario, "UTF-8")}" +
                            "&departamento=${URLEncoder.encode(departamento, "UTF-8")}" +
                            "&turno=${URLEncoder.encode(turno, "UTF-8")}"

                conexion.outputStream.write(data.toByteArray())
                val respuesta = conexion.inputStream.bufferedReader().readText().trim()

                runOnUiThread {
                    if (respuesta.startsWith("OK|")) {
                        val datos = respuesta.removePrefix("OK|")
                        if (datos.isNotBlank()) {
                            val filas = datos.split(";")
                            for (f in filas) {
                                val partes = f.split(":")
                                if (partes.size == 2) agregarFila(partes[0], partes[1])
                            }
                        }
                    } else if (respuesta != "OK|") {
                        Toast.makeText(this, "Respuesta: $respuesta", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error al cargar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    private fun guardarProductos() {
        val productosList = ArrayList<String>()

        for (i in 0 until contenedor.childCount) {
            val fila = contenedor.getChildAt(i) as LinearLayout
            val nombre = (fila.getChildAt(0) as EditText).text.toString().trim()
            val cantidad = (fila.getChildAt(1) as EditText).text.toString().trim()

            val nombreLimpio = nombre.replace(",", "").replace(":", "")

            if (nombreLimpio.isNotEmpty() && cantidad.isNotEmpty()) {
                productosList.add("$nombreLimpio:$cantidad")
            }
        }

        if (productosList.isEmpty()) {
            Toast.makeText(this, "No hay productos válidos para guardar", Toast.LENGTH_SHORT).show()
            return
        }

        Thread {
            try {
                val url = URL("http://10.0.2.2/inventario/guardar_productos.php")
                val conexion = url.openConnection() as HttpURLConnection
                conexion.requestMethod = "POST"
                conexion.doOutput = true
                conexion.connectTimeout = 5000

                // ✅ 2. Aquí añadimos el turno a la cadena POST
                val data = "usuario=${URLEncoder.encode(usuario, "UTF-8")}" +
                        "&departamento=${URLEncoder.encode(departamento, "UTF-8")}" +
                        "&turno=${URLEncoder.encode(turno, "UTF-8")}" +
                        "&productos=${URLEncoder.encode(productosList.joinToString(","), "UTF-8")}"

                conexion.outputStream.write(data.toByteArray())

                val respuesta = conexion.inputStream.bufferedReader().readText().trim()

                runOnUiThread {
                    if (respuesta == "OK") {
                        Toast.makeText(this, "¡Guardado en Base de Datos!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Fallo: $respuesta", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }
}