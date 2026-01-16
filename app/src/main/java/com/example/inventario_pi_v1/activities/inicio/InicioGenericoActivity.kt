package com.example.inventario_pi_v1.activities.inicio

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.inventario_pi_v1.R
import com.example.inventario_pi_v1.activities.departamentos.DepartamentosActivity

class InicioGenericoActivity : AppCompatActivity() {

    private lateinit var tvBienvenida: TextView
    private lateinit var tvRol: TextView
    private lateinit var btnAdminRegistro: Button
    private lateinit var btnGenerarInventario: Button

    private lateinit var btnReturn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_generico)

        tvBienvenida = findViewById(R.id.tvBienvenidaGenerica)
        tvRol = findViewById(R.id.tvRolActual)
        btnAdminRegistro = findViewById(R.id.btnAdminRegistro)
        btnGenerarInventario = findViewById(R.id.btnGenerarInventario)
        btnReturn = findViewById(R.id.btnReturn)

        val usuario = intent.getStringExtra("USUARIO") ?: "Usuario"
        val rol = intent.getStringExtra("ROL") ?: ""

        tvBienvenida.text = "¡Hola, $usuario!"
        tvRol.text = "Sesión iniciada como: $rol"

        if (rol.uppercase() == "ADMIN") {
            btnAdminRegistro.visibility = View.VISIBLE
            btnAdminRegistro.setOnClickListener {
                startActivity(Intent(this, RegistroActivity::class.java))
            }
        }

        btnGenerarInventario.setOnClickListener {
            startActivity(Intent(this, DepartamentosActivity::class.java))
            //finish()

        }

        btnReturn.setOnClickListener {

            finish()
        }
    }
}