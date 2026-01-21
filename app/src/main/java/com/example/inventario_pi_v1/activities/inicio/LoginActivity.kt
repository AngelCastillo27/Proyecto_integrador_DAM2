package com.example.inventario_pi_v1.activities.inicio

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inventario_pi_v1.R
import com.example.inventario_pi_v1.activities.inicio.InicioGenericoActivity
import com.example.inventario_pi_v1.network.Conexion
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {

    private lateinit var rolSeleccionado: String

    private lateinit var etUsuario: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnReturn: Button // ✅ declaramos el botón

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // ENLAZAR VISTAS con variables
        etUsuario = findViewById(R.id.etUsuario)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnReturn = findViewById(R.id.btnReturn) // ✅ enlazamos el botón

        rolSeleccionado = intent.getStringExtra("ROL_SELECCIONADO") ?: ""

        btnLogin.setOnClickListener {
            val usuario = etUsuario.text.toString()
            val password = etPassword.text.toString()

            if (usuario.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Complete los campos", Toast.LENGTH_SHORT).show()
            } else {
                login(usuario, password)
            }
        }

        // 🔙 BOTÓN REGRESAR → vuelve a SelectorRolActivity
        btnReturn.setOnClickListener {
            finish()
        }
    }

    private fun login(usuario: String, password: String) {
        Thread {
            try {
                val url = URL(Conexion.URL_LOGIN)
                val conexion = url.openConnection() as HttpURLConnection
                conexion.requestMethod = "POST"
                conexion.doOutput = true

                val datos = "usuario=$usuario&password=$password"
                conexion.outputStream.write(datos.toByteArray())

                val respuesta = conexion.inputStream.bufferedReader().readLine()

                runOnUiThread {
                    val respuestaLimpia = respuesta?.trim() ?: ""

                    if (respuestaLimpia.startsWith("ok")) {
                        val partes = respuestaLimpia.split("|")

                        val usuarioBD = if (partes.size > 1) partes[1].trim() else ""
                        val rolBD = if (partes.size > 2) partes[2].trim() else ""

                        if (rolBD.equals(rolSeleccionado, ignoreCase = true)) {
                            val intent = Intent(this, InicioGenericoActivity::class.java)
                            intent.putExtra("USUARIO", usuarioBD)
                            intent.putExtra("ROL", rolBD)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Elegiste $rolSeleccionado pero eres $rolBD",
                                Toast.LENGTH_LONG
                            ).show()
                        }


                } else {
                        Toast.makeText(
                            this,
                            "Usuario o contraseña incorrectos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
}
