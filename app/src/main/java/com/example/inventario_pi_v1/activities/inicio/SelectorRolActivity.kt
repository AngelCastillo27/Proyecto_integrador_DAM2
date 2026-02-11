package com.example.inventario_pi_v1.activities.inicio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.inventario_pi_v1.R
import com.example.inventario_pi_v1.network.LocaleHelper

class SelectorRolActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector_rol)

        findViewById<Button>(R.id.btnAdmin).setOnClickListener { goToLogin("ADMIN") }
        findViewById<Button>(R.id.btnOperario).setOnClickListener { goToLogin("OPERARIO") }
        findViewById<Button>(R.id.btnReturn).setOnClickListener { finish() }

        // Botones de idioma
        findViewById<ImageButton>(R.id.btnEs).setOnClickListener { aplicarCambioIdioma("es") }
        findViewById<ImageButton>(R.id.btnEn).setOnClickListener { aplicarCambioIdioma("en") }
        findViewById<ImageButton>(R.id.btnZh).setOnClickListener { aplicarCambioIdioma("zh") }
        findViewById<ImageButton>(R.id.btnIt).setOnClickListener { aplicarCambioIdioma("it") }
        findViewById<ImageButton>(R.id.btnFr).setOnClickListener { aplicarCambioIdioma("fr") }
    }

    // 2. Función de cambio suave
    private fun aplicarCambioIdioma(lang: String) {
        // Cambiamos el idioma en el Helper
        LocaleHelper.setLocale(this, lang)

        // Reiniciamos la actividad con una transición de fundido (fade)
        // Esto ELIMINA la pantalla negra
        val intent = intent
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    private fun goToLogin(rol: String) {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("ROL_SELECCIONADO", rol)
        startActivity(intent)
    }
}
