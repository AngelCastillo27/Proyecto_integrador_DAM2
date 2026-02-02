package com.example.inventario_pi_v1.activities.inicio

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.inventario_pi_v1.R


class BienvenidaCeroActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_bienvenida_cero)

            val imgLogo = findViewById<ImageView>(R.id.btnLogoBienvenida)

            imgLogo.setOnClickListener {
                val intent = Intent(this, SelectorRolActivity::class.java)
                startActivity(intent)
            }
        }
    }

