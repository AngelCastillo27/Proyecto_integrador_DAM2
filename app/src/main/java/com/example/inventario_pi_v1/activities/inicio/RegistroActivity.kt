package com.example.inventario_pi_v1.activities

import android.content.Intent
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
    private lateinit var btnReturnAdm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        etUsuario = findViewById(R.id.etUsuario)
        etPassword = findViewById(R.id.etPassword)
        etNombres = findViewById(R.id.etNombres)
        etApellidos = findViewById(R.id.etApellidos)
        spinnerRol = findViewById(R.id.spinnerRol)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        btnReturnAdm = findViewById(R.id.btnReturn_adm)

        val roles = arrayOf("ADMIN", "OPERARIO")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRol.adapter = adapter

        btnRegistrar.setOnClickListener {
            registrarUsuario()
        }

        btnReturnAdm.setOnClickListener {

            finish()
        }
    }

    private fun registrarUsuario() {
        val usuario = etUsuario.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val nombres = etNombres.text.toString().trim()
        val apellidos = etApellidos.text.toString().trim()
        val rol = spinnerRol.selectedItem.toString()

        if (usuario.isEmpty() || password.isEmpty() || nombres.isEmpty() || apellidos.isEmpty()) {
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
                conexion.outputStream.flush()

                val respuesta = conexion.inputStream.bufferedReader().readLine().trim()

                runOnUiThread {
                    when (respuesta) {
                        "USUARIO_REGISTRADO" -> {
                            Toast.makeText(
                                this,
                                "Usuario registrado correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }

                        "ERROR_USUARIO_EXISTE" -> {
                            Toast.makeText(
                                this,
                                "Este usuario ya existe",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> {
                            Toast.makeText(
                                this,
                                "Error al registrar usuario",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Error al conectar con el servidor",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.start()
    }
}
