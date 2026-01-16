package com.example.inventario_pi_v1.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.inventario_pi_v1.R

class SelectorRolActivity : AppCompatActivity() {
    // Dentro de SelectorRolActivity.kt
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector_rol)

        val btnAdmin = findViewById<Button>(R.id.btnAdmin)
        val btnOperario = findViewById<Button>(R.id.btnOperario)
        val btnReturn = findViewById<Button>(R.id.btnReturn) // Agregado para que no de error

        btnAdmin.setOnClickListener { goToLogin("ADMIN") }
        btnOperario.setOnClickListener { goToLogin("OPERARIO") }

        btnReturn.setOnClickListener {
            finish() // O la lógica que desees para salir de la app
        }
    }

    private fun goToLogin(rol: String) {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("ROL_SELECCIONADO", rol)
        startActivity(intent)
    }
}