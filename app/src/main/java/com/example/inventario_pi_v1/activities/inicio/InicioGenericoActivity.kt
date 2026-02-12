package com.example.inventario_pi_v1.activities.inicio

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.inventario_pi_v1.R
import com.example.inventario_pi_v1.activities.departamentos.DepartamentosActivity
import com.example.inventario_pi_v1.activities.departamentos.ReportesActivity
import com.example.inventario_pi_v1.network.LocaleHelper

class InicioGenericoActivity : AppCompatActivity() {

    private lateinit var tvBienvenida: TextView
    private lateinit var tvRol: TextView
    private lateinit var btnAdminRegistro: Button
    private lateinit var btnGenerarInventario: Button
    private lateinit var btnFinalizarInventario: Button
    private lateinit var btnReturn: Button
    private lateinit var btnVerReportes: Button // Declaramos el botón

    private var usuarioId: Int = 0
    private lateinit var usuario: String
    private lateinit var rol: String

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_generico)

        tvBienvenida = findViewById(R.id.tvBienvenidaGenerica)
        tvRol = findViewById(R.id.tvRolActual)
        btnAdminRegistro = findViewById(R.id.btnAdminRegistro)
        btnGenerarInventario = findViewById(R.id.btnGenerarInventario)
        btnFinalizarInventario = findViewById(R.id.btnFinalizarInventario)
        btnReturn = findViewById(R.id.btnReturn)
        btnVerReportes = findViewById(R.id.btnVerReportes) // Enlazamos el botón

        usuarioId = intent.getIntExtra("USUARIO_ID", 0)
        usuario = intent.getStringExtra("USUARIO") ?: ""
        rol = intent.getStringExtra("ROL") ?: ""

        tvBienvenida.text = "${getString(R.string.hola)}, $usuario!"
        tvRol.text = "${getString(R.string.sesion_como)}: $rol"

        if (rol.uppercase() == "ADMIN") {
            btnAdminRegistro.visibility = View.VISIBLE
            btnAdminRegistro.setOnClickListener {
                startActivity(Intent(this, RegistroActivity::class.java))
            }
        } else {
            btnAdminRegistro.visibility = View.GONE
        }

        btnGenerarInventario.setOnClickListener { mostrarSeleccionTurno() }
        btnFinalizarInventario.setOnClickListener { confirmarFinalizarInventario() }
        btnReturn.setOnClickListener { finish() }

        // ✅ FUNCIONALIDAD RESTAURADA
        btnVerReportes.setOnClickListener {
            val i = Intent(this, ReportesActivity::class.java)
            i.putExtra("USUARIO", usuario)
            i.putExtra("ROL", rol)
            startActivity(i)
        }

        // Botones de idioma
        findViewById<ImageButton>(R.id.btnEs).setOnClickListener { aplicarCambioIdioma("es") }
        findViewById<ImageButton>(R.id.btnEn).setOnClickListener { aplicarCambioIdioma("en") }
        findViewById<ImageButton>(R.id.btnZh).setOnClickListener { aplicarCambioIdioma("zh") }
        findViewById<ImageButton>(R.id.btnIt).setOnClickListener { aplicarCambioIdioma("it") }
        findViewById<ImageButton>(R.id.btnFr).setOnClickListener { aplicarCambioIdioma("fr") }
    }

    private fun aplicarCambioIdioma(lang: String) {
        LocaleHelper.setLocale(this, lang)
        val intentActual = intent
        finish()
        startActivity(intentActual)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun mostrarSeleccionTurno() {
        val turnos = arrayOf(getString(R.string.manana), getString(R.string.tarde), getString(R.string.noche))
        val codigos = arrayOf("M", "T", "N")

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.selecciona_turno))
            .setItems(turnos) { _, which ->
                val turnoSeleccionado = codigos[which]
                val i = Intent(this, DepartamentosActivity::class.java)
                i.putExtra("USUARIO_ID", usuarioId)
                i.putExtra("USUARIO", usuario)
                i.putExtra("TURNO", turnoSeleccionado)
                startActivity(i)
            }
            .show()
    }

    private fun confirmarFinalizarInventario() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.finalizar_inventario))
            .setMessage(getString(R.string.mensaje_finalizar))
            .setPositiveButton(getString(R.string.si)) { _, _ -> finalizarInventario() }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun finalizarInventario() {
        val url = "http://10.0.2.2/inventario/finalizar_inventario.php"
        val request = object : StringRequest(Request.Method.POST, url, { response ->
            if (response.contains("INVENTARIO_FINALIZADO_CON_EXITO")) {
                Toast.makeText(this, getString(R.string.inventario_cerrado), Toast.LENGTH_LONG).show()
                finish()
            }
        }, {
            Toast.makeText(this, getString(R.string.error_conexion), Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): MutableMap<String, String> = hashMapOf("usuario" to usuario)
        }
        Volley.newRequestQueue(this).add(request)
    }
}
