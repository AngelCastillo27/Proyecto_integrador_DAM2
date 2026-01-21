package com.example.inventario_pi_v1.model

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.inventario_pi_v1.R

class SnacksAdapter(
    private val lista: MutableList<Snack>
) : RecyclerView.Adapter<SnacksAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val etNombre: EditText = view.findViewById(R.id.etNombre)
        val etCantidad: EditText = view.findViewById(R.id.etCantidad)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_snack, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val snack = lista[position]

        // Evita duplicar TextWatchers
        holder.etNombre.addTextChangedListener(null)
        holder.etCantidad.addTextChangedListener(null)

        holder.etNombre.setText(snack.nombre)
        holder.etCantidad.setText(snack.cantidad.toString())

        holder.btnEliminar.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                lista.removeAt(pos)
                notifyItemRemoved(pos)
            }
        }

        holder.etNombre.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                snack.nombre = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        holder.etCantidad.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                snack.cantidad = s.toString().toIntOrNull() ?: 0
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun getItemCount(): Int = lista.size

    fun agregarSnack() {
        lista.add(Snack("", 0))
        notifyItemInserted(lista.size - 1)
    }

    fun obtenerLista(): List<Snack> {
        return lista
    }
}
