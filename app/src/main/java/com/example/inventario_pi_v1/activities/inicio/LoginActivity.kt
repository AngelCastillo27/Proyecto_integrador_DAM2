package com.example.inventario_pi_v1.activities.inicio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inventario_pi_v1.R
import com.example.inventario_pi_v1.network.Conexion
import com.example.inventario_pi_v1.network.LocaleHelper
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {

    private lateinit var rolSeleccionado: String
    private lateinit var etUsuario: EditText
    private lateinit var etPassword: EditText

    override fun attachBaseContext(newBase: Context) {
        // OBLIGATORIO: Ancla el idioma guardado al iniciar la actividad
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsuario = findViewById(R.id.etUsuario)
        etPassword = findViewById(R.id.etPassword)
        rolSeleccionado = intent.getStringExtra("ROL_SELECCIONADO") ?: ""

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val usuario = etUsuario.text.toString()
            val password = etPassword.text.toString()
            if (usuario.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_conexion), Toast.LENGTH_SHORT).show()
            } else {
                login(usuario, password)
            }
        }

        findViewById<Button>(R.id.btnReturn).setOnClickListener { finish() }

        // Botones de idioma con corrección
        findViewById<ImageButton>(R.id.btnEs).setOnClickListener { aplicarCambioIdioma("es") }
        findViewById<ImageButton>(R.id.btnEn).setOnClickListener { aplicarCambioIdioma("en") }
        findViewById<ImageButton>(R.id.btnZh).setOnClickListener { aplicarCambioIdioma("zh") }
        findViewById<ImageButton>(R.id.btnIt).setOnClickListener { aplicarCambioIdioma("it") }
        findViewById<ImageButton>(R.id.btnFr).setOnClickListener { aplicarCambioIdioma("fr") }
    }

    private fun aplicarCambioIdioma(lang: String) {
        // Pasamos el contexto (this) para que no dé error
        LocaleHelper.setLocale(this, lang)

        // Reinicio suave para evitar la pantalla negra y el doble clic
        val intent = intent
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
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
                    if (respuesta?.trim()?.startsWith("ok") == true) {
                        val partes = respuesta.split("|")
                        val usuarioBD = if (partes.size > 1) partes[1].trim() else ""
                        val rolBD = if (partes.size > 2) partes[2].trim() else ""

                        if (rolBD.equals(rolSeleccionado, ignoreCase = true)) {
                            val intent = Intent(this, InicioGenericoActivity::class.java)
                            intent.putExtra("USUARIO", usuarioBD)
                            intent.putExtra("ROL", rolBD)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Rol incorrecto", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread { Toast.makeText(this, getString(R.string.error_conexion), Toast.LENGTH_SHORT).show() }
            }
        }.start()
    }
}
