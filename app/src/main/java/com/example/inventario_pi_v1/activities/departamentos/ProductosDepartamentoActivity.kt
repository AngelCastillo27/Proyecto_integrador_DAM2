package com.example.inventario_pi_v1.activities.departamentos

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.inventario_pi_v1.R

class ProductosDepartamentoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos_departamento)

        val tvTitulo = findViewById<TextView>(R.id.tvTituloDepartamento)

        val departamento = intent.getStringExtra("DEPARTAMENTO") ?: "Departamento"

        tvTitulo.text = departamento
    }
}
