package com.example.inventario_pi_v1.activities.departamentos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.inventario_pi_v1.R
import com.example.inventario_pi_v1.activities.departamentos.ProductosActivity


class DepartamentosActivity : AppCompatActivity() {

    private lateinit var btnSnacks: LinearLayout
    private lateinit var btnLicor: LinearLayout

    private lateinit var btnTabaco: LinearLayout

    private lateinit var btnComplementos: LinearLayout

    private lateinit var btnOtros: LinearLayout


    private fun abrirDepartamento(nombre: String) {
        val usuario = intent.getStringExtra("USUARIO") ?: ""

        val i = Intent(this, ProductosActivity::class.java)
        i.putExtra("USUARIO", usuario)
        i.putExtra("DEPARTAMENTO", nombre)
        startActivity(i)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_departamentos)

        // Referencias
        btnSnacks = findViewById(R.id.btnSnacks)
        btnLicor = findViewById(R.id.btnLicor)
        btnTabaco = findViewById(R.id.btnTabaco)
        btnComplementos = findViewById(R.id.btnComplementos)
        btnOtros = findViewById(R.id.btnOtros)


        // Clicks
        btnSnacks.setOnClickListener {
            abrirDepartamento("SNACKS")
        }

        btnLicor.setOnClickListener {
            abrirDepartamento("LICOR")
        }

        btnTabaco.setOnClickListener {
            abrirDepartamento("TABACO")
        }

        btnComplementos.setOnClickListener {
            abrirDepartamento("COMPLEMENTOS")
        }


        btnOtros.setOnClickListener {
            abrirDepartamento("OTROS")
        }



        findViewById<Button>(R.id.btnRetorno_departamentos).setOnClickListener {
            finish()
        }
    }
}
