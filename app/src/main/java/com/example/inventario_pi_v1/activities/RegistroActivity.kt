package com.example.inventario_pi_v1.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.inventario_pi_v1.R
import com.example.inventario_pi_v1.network.Conexion
import java.net.HttpURLConnection
import java.net.URL

class RegistroActivity : AppCompatActivity() {

    private lateinit var etUsuario: EditText
    private lateinit var etPassword: EditText
    private lateinit var etNombres: EditText
    private lateinit var etApellidos: EditText
    private lateinit var spinnerRol: Spinner
    private lateinit var btnRegistrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // ENLAZAR VISTAS
        etUsuario = findViewById(R.id.etUsuario)
        etPassword = findViewById(R.id.etPassword)
        etNombres = findViewById(R.id.etNombres)
        etApellidos = findViewById(R.id.etApellidos)
        spinnerRol = findViewById(R.id.spinnerRol)
        btnRegistrar = findViewById(R.id.btnRegistrar)

        // LLENAR EL SPINNER CON ROLES
        val roles = arrayOf("ADMIN", "OPERARIO")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRol.adapter = adapter

        btnRegistrar.setOnClickListener {
            registrarUsuario()
        }
    }

    private fun registrarUsuario() {
        val usuario = etUsuario.text.toString()
        val password = etPassword.text.toString()
        val nombres = etNombres.text.toString()
        val apellidos = etApellidos.text.toString()
        val rol = spinnerRol.selectedItem.toString()

        if(usuario.isEmpty() || password.isEmpty() || nombres.isEmpty() || apellidos.isEmpty()){
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        Thread {
            try {
                val url = URL(Conexion.URL_REGISTRO)
                val conexion = url.openConnection() as HttpURLConnection
                conexion.requestMethod = "POST"
                conexion.doOutput = true

                val datos =
                    "usuario=$usuario&password=$password&nombres=$nombres&apellidos=$apellidos&rol=$rol"

                conexion.outputStream.write(datos.toByteArray())

                val respuesta = conexion.inputStream.bufferedReader().readLine()

                runOnUiThread {
                    Toast.makeText(this, respuesta, Toast.LENGTH_SHORT).show()
                    if (respuesta.contains("correctamente")) {
                        finish()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
}
