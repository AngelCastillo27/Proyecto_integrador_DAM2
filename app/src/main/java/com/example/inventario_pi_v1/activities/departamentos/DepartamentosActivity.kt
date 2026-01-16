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

        // Función reutilizable
        fun abrirDepartamento(id: Int, nombre: String) {
            findViewById<LinearLayout>(id).setOnClickListener {
                val intent = Intent(this, ProductosDepartamentoActivity::class.java)
                intent.putExtra("DEPARTAMENTO", nombre)
                startActivity(intent)
            }
        }

        abrirDepartamento(R.id.btnSnacks, "Snacks")
        abrirDepartamento(R.id.btnFrutosSecos, "Frutos Secos")
        abrirDepartamento(R.id.btnGaseosas, "Refrescos")
        abrirDepartamento(R.id.btnAlcohol, "Bebidas alcohólicas")
        abrirDepartamento(R.id.btnCigarros, "Tabaco")
        abrirDepartamento(R.id.btnFrutas, "Frutas")
        abrirDepartamento(R.id.btnLacteos, "Lácteos")
        abrirDepartamento(R.id.btnPanaderia, "Panadería")
        abrirDepartamento(R.id.btnAlimentos, "Legumbres")
        abrirDepartamento(R.id.btnCuidadoPersonal, "Cuidado Personal")
        abrirDepartamento(R.id.btnHogar, "Hogar")
        abrirDepartamento(R.id.btnOtros, "Otros")

        // Botón regresar
        findViewById<Button>(R.id.btnRetorno_depgen).setOnClickListener {
            finish()
        }
    }
}
