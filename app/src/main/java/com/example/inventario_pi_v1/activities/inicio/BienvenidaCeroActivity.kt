package com.example.inventario_pi_v1.activities.inicio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.inventario_pi_v1.R
import com.example.inventario_pi_v1.network.LocaleHelper

class BienvenidaCeroActivity : AppCompatActivity() {

    // 1. OBLIGATORIO: Esto fuerza el idioma (Español por defecto) nada más abrir la app
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenida_cero)

        val imgLogo = findViewById<ImageView>(R.id.btnLogoBienvenida)
        imgLogo.setOnClickListener {
            val intent = Intent(this, SelectorRolActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}