package com.example.proyectfaseii.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectfaseii.R
import com.example.proyectfaseii.data.models.Habito

class HabitosAdapter(
    private var listaHabitos: List<Habito>,
    private val onClick: (Habito) -> Unit
) : RecyclerView.Adapter<HabitosAdapter.HabitoViewHolder>() {

    inner class HabitoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoria)

        init {
            itemView.setOnClickListener {
                onClick(listaHabitos[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habito, parent, false)
        return HabitoViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitoViewHolder, position: Int) {
        val habito = listaHabitos[position]
        holder.tvNombre.text = habito.nombre
        holder.tvCategoria.text = habito.categoria
    }

    override fun getItemCount(): Int = listaHabitos.size

    fun updateData(nuevaLista: List<Habito>) {
        listaHabitos = nuevaLista
        notifyDataSetChanged()
    }
}
