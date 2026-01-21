package com.example.inventario_pi_v1.activities.departamentos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.inventario_pi_v1.R

class DepartamentosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_departamentos)

        // SOLO SNACKS
        findViewById<LinearLayout>(R.id.btnSnacks).setOnClickListener {
            val usuario = intent.getStringExtra("USUARIO") ?: ""

            findViewById<LinearLayout>(R.id.btnSnacks).setOnClickListener {
                val i = Intent(this, SnacksActivity::class.java)
                i.putExtra("USUARIO", usuario)
                startActivity(i)
            }

        }

        findViewById<Button>(R.id.btnRetorno_departamentos).setOnClickListener {
            finish()
        }
    }
}
