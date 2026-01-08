package com.example.inventario_pi_v1.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.inventario_pi_v1.R

class InicioGenericoActivity : AppCompatActivity() {

    private lateinit var tvBienvenida: TextView
    private lateinit var tvRol: TextView
    private lateinit var btnAdminRegistro: Button
    private lateinit var btnGenerarInventario: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_generico)

        tvBienvenida = findViewById(R.id.tvBienvenidaGenerica)
        tvRol = findViewById(R.id.tvRolActual)
        btnAdminRegistro = findViewById(R.id.btnAdminRegistro)
        btnGenerarInventario = findViewById(R.id.btnGenerarInventario)

        val usuario = intent.getStringExtra("USUARIO") ?: "Usuario"
        val rol = intent.getStringExtra("ROL")?.trim()?.uppercase() ?: ""

        tvBienvenida.text = "¡Hola, $usuario!"
        tvRol.text = "Sesión iniciada como: $rol"

        // LÓGICA DE PERMISOS
        if (rol == "ADMIN") {
            btnAdminRegistro.visibility = View.VISIBLE
            btnAdminRegistro.setOnClickListener {
                startActivity(Intent(this, RegistroActivity::class.java))
            }
        }

        btnGenerarInventario.setOnClickListener {
            // Aquí irá tu lógica para empezar el inventario
        }
    }
}