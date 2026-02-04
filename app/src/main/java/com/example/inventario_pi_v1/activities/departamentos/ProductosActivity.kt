package com.example.inventario_pi_v1.activities.departamentos

import android.os.Bundle
import android.text.InputType
import android.view.View
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)

        usuario = intent.getStringExtra("USUARIO") ?: ""
        departamento = intent.getStringExtra("DEPARTAMENTO") ?: ""

        findViewById<TextView>(R.id.tvTitulo).text = departamento
        contenedor = findViewById(R.id.contenedorProductos)

        findViewById<Button>(R.id.btnAgregar).setOnClickListener { agregarFila("", "", false) }
        findViewById<Button>(R.id.btnGuardar).setOnClickListener { guardarProductos() }
        findViewById<Button>(R.id.btnRetorno_depgen).setOnClickListener { finish() }

        cargarProductos()
    }

    private fun agregarFila(nombre: String, cantidad: String, esEstatico: Boolean) {
        val fila = LinearLayout(this)
        fila.orientation = LinearLayout.HORIZONTAL
        fila.setPadding(8, 8, 8, 8)

        val etNombre = EditText(this)
        etNombre.hint = "Producto"
        etNombre.setText(nombre)
        etNombre.isEnabled = !esEstatico // Si es estático, no se puede cambiar el nombre
        etNombre.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)

        val etCantidad = EditText(this)
        etCantidad.hint = "Cant."
        etCantidad.inputType = InputType.TYPE_CLASS_NUMBER
        etCantidad.setText(if (cantidad == "0") "" else cantidad)
        etCantidad.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

        val btnEliminar = Button(this)
        btnEliminar.text = "❌"
        if (esEstatico) {
            btnEliminar.visibility = View.INVISIBLE // No se puede borrar si es estático
        } else {
            btnEliminar.setOnClickListener { contenedor.removeView(fila) }
        }

        fila.addView(etNombre)
        fila.addView(etCantidad)
        fila.addView(btnEliminar)

        // Si es estático lo ponemos al final, si es nuevo al principio
        if (esEstatico) contenedor.addView(fila) else contenedor.addView(fila, 0)
    }

    private fun cargarProductos() {
        Thread {
            try {
                val url = URL("http://10.0.2.2/inventario/listar_productos.php")
                val conexion = url.openConnection() as HttpURLConnection
                conexion.requestMethod = "POST"
                conexion.doOutput = true
                val data = "usuario=${URLEncoder.encode(usuario, "UTF-8")}&departamento=${URLEncoder.encode(departamento, "UTF-8")}"
                conexion.outputStream.write(data.toByteArray())
                val respuesta = conexion.inputStream.bufferedReader().readText().trim()

                runOnUiThread {
                    if (respuesta.startsWith("OK|")) {
                        val datos = respuesta.removePrefix("OK|")
                        if (datos.isNotBlank()) {
                            val filas = datos.split(";")
                            for (f in filas) {
                                val partes = f.split(":")
                                if (partes.size >= 3) {
                                    agregarFila(partes[0], partes[1], partes[2] == "1")
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun guardarProductos() {
        val productosList = ArrayList<String>()
        for (i in 0 until contenedor.childCount) {
            val fila = contenedor.getChildAt(i) as LinearLayout
            val nombre = (fila.getChildAt(0) as EditText).text.toString().trim()
            val cantidad = (fila.getChildAt(1) as EditText).text.toString().trim()
            val cantFinal = if (cantidad.isEmpty()) "0" else cantidad
            if (nombre.isNotEmpty()) productosList.add("$nombre:$cantFinal")
        }

        Thread {
            try {
                val url = URL("http://10.0.2.2/inventario/guardar_productos.php")
                val conexion = url.openConnection() as HttpURLConnection
                conexion.requestMethod = "POST"
                conexion.doOutput = true
                val data = "usuario=${URLEncoder.encode(usuario, "UTF-8")}" +
                        "&departamento=${URLEncoder.encode(departamento, "UTF-8")}" +
                        "&productos=${URLEncoder.encode(productosList.joinToString(","), "UTF-8")}"
                conexion.outputStream.write(data.toByteArray())
                val respuesta = conexion.inputStream.bufferedReader().readText().trim()
                runOnUiThread {
                    if (respuesta == "OK") Toast.makeText(this, "Guardado correctamente", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) { e.printStackTrace() }
        }.start()
    }
}
