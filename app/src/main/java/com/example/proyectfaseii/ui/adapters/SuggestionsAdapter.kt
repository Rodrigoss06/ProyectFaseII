package com.example.proyectfaseii.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectfaseii.R
import com.example.proyectfaseii.data.models.Habito

class SuggestionsAdapter(
    private var listaSuggestions: List<Habito>,
    private val onAddClick: (Habito) -> Unit
) : RecyclerView.Adapter<SuggestionsAdapter.SuggestionViewHolder>() {

    inner class SuggestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvSuggestionName)
        val tvCategory: TextView = itemView.findViewById(R.id.tvSuggestionCategory)
        val btnAdd: Button = itemView.findViewById(R.id.btnAddSuggestion)

        init {
            btnAdd.setOnClickListener {
                onAddClick(listaSuggestions[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_suggestion, parent, false)
        return SuggestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        val habit = listaSuggestions[position]
        holder.tvName.text = habit.name
        holder.tvCategory.text = habit.area?.name ?: "Sin categoría"
    }

    override fun getItemCount(): Int = listaSuggestions.size

    fun updateData(nuevaLista: List<Habito>) {
        listaSuggestions = nuevaLista
        notifyDataSetChanged()
    }
}
