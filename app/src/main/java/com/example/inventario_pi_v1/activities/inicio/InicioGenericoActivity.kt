package com.example.inventario_pi_v1.activities.inicio

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.inventario_pi_v1.R
import com.example.inventario_pi_v1.activities.departamentos.DepartamentosActivity

class InicioGenericoActivity : AppCompatActivity() {

    private lateinit var tvBienvenida: TextView
    private lateinit var tvRol: TextView
    private lateinit var btnAdminRegistro: Button
    private lateinit var btnGenerarInventario: Button
    private lateinit var btnFinalizarInventario: Button
    private lateinit var btnReturn: Button

    private var usuarioId: Int = 0
    private lateinit var usuario: String
    private lateinit var rol: String
    private var turno: String = "M" 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_generico)

        tvBienvenida = findViewById(R.id.tvBienvenidaGenerica)
        tvRol = findViewById(R.id.tvRolActual)
        btnAdminRegistro = findViewById(R.id.btnAdminRegistro)
        btnGenerarInventario = findViewById(R.id.btnGenerarInventario)
        btnFinalizarInventario = findViewById(R.id.btnFinalizarInventario)
        btnReturn = findViewById(R.id.btnReturn)

        usuarioId = intent.getIntExtra("USUARIO_ID", 0)
        usuario = intent.getStringExtra("USUARIO") ?: ""
        rol = intent.getStringExtra("ROL") ?: ""

        tvBienvenida.text = "¡Hola, $usuario!"
        tvRol.text = "Sesión iniciada como: $rol"

        // SOLO ADMIN
        if (rol.uppercase() == "ADMIN") {
            btnAdminRegistro.visibility = View.VISIBLE
            btnAdminRegistro.setOnClickListener {
                startActivity(Intent(this, RegistroActivity::class.java))
            }
        } else {
            btnAdminRegistro.visibility = View.GONE
        }

        btnGenerarInventario.setOnClickListener {
            val i = Intent(this, DepartamentosActivity::class.java)
            i.putExtra("USUARIO_ID", usuarioId)
            i.putExtra("USUARIO", usuario) // ✅ CORRECCIÓN: Ahora pasamos el nombre del usuario
            startActivity(i)
        }

        btnFinalizarInventario.setOnClickListener {
            confirmarFinalizarInventario()
        }

        btnReturn.setOnClickListener {
            finish()
        }
    }

    private fun confirmarFinalizarInventario() {
        AlertDialog.Builder(this)
            .setTitle("Finalizar inventario")
            .setMessage("Esto guardará el inventario y reiniciará los productos. ¿Deseas continuar?")
            .setPositiveButton("Sí") { _, _ ->
                finalizarInventario()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun finalizarInventario() {
        val url = "http://10.0.2.2/inventario/finalizar_inventario.php"

        val request = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                if (response.contains("INVENTARIO_FINALIZADO_CON_EXITO")) {
                    Toast.makeText(this, "✅ Inventario guardado y cerrado", Toast.LENGTH_LONG).show()
                    // Opcional: Cerrar la actividad para que el usuario tenga que entrar de nuevo
                    finish()
                } else {
                    Toast.makeText(this, "Aviso: $response", Toast.LENGTH_LONG).show()
                }
            },
            {
                Toast.makeText(this, "Error de conexión al finalizar", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf("usuario" to usuario)
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

}
