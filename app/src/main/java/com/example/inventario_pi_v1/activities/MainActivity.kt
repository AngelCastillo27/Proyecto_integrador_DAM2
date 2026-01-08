package com.example.inventario_pi_v1.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.inventario_pi_v1.R

class MainActivity : AppCompatActivity() {
    private lateinit var tvBienvenida: TextView
    private lateinit var btnRegistrarUsuario: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvBienvenida = findViewById(R.id.tvBienvenida)
        btnRegistrarUsuario = findViewById(R.id.btnRegistrarUsuario)

        // Recuperamos los datos del Intent
        val usuario = intent.getStringExtra("USUARIO") ?: "Usuario"
        // Limpiamos el rol de espacios y lo pasamos a mayúsculas para asegurar la comparación
        val rol = intent.getStringExtra("ROL")?.trim()?.uppercase() ?: ""

        tvBienvenida.text = "Bienvenido $usuario\nRol: $rol"

        // LÓGICA DE PERMISOS: Usamos equals para comparar strings de forma segura
        if (rol == "ADMIN") {
            btnRegistrarUsuario.visibility = View.VISIBLE
            btnRegistrarUsuario.setOnClickListener {
                startActivity(Intent(this, RegistroActivity::class.java))
            }
        } else {
            // Si no es ADMIN (es OPERARIO), desaparece el botón
            btnRegistrarUsuario.visibility = View.GONE
        }
    }
}