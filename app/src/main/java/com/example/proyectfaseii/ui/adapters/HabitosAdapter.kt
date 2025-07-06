package com.example.proyectfaseii.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
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
        val progressGoal: ProgressBar = itemView.findViewById(R.id.progressGoal)

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
        holder.tvNombre.text = habito.name
        holder.tvCategoria.text = habito.area?.name ?: "Sin categoría"

        // 👇 Calculate progress based on goal.value and goal_history_items size
        val goalValue = habito.goal.value
        val completed = habito.goal_history_items.size.toDouble()

        val progress = if (goalValue > 0) ((completed / goalValue) * 100).toInt() else 0
        holder.progressGoal.progress = progress
    }

    override fun getItemCount(): Int = listaHabitos.size

    fun updateData(nuevaLista: List<Habito>) {
        listaHabitos = nuevaLista
        notifyDataSetChanged()
    }
}
