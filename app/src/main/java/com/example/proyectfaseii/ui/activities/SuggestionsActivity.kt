package com.example.proyectfaseii.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectfaseii.R
import com.example.proyectfaseii.data.api.RetrofitClient
import com.example.proyectfaseii.data.models.Habito
import com.example.proyectfaseii.ui.adapters.SuggestionsAdapter
import com.example.proyectfaseii.utils.SharedPrefManager
import kotlinx.coroutines.launch

/**
 * Muestra una lista de hábitos sugeridos y permite añadirlos al usuario.
 */
class SuggestionsActivity : AppCompatActivity() {

    private lateinit var rvSuggestions: RecyclerView
    private lateinit var adapter: SuggestionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggestions)

        rvSuggestions = findViewById(R.id.rvSuggestions)
        adapter = SuggestionsAdapter(emptyList()) { habit ->
            addSuggestedHabit(habit)
        }
        rvSuggestions.layoutManager = LinearLayoutManager(this)
        rvSuggestions.adapter = adapter

        loadSuggestions()
    }

    private fun loadSuggestions() {
        val userId = SharedPrefManager.getInstance(this).getUserId() ?: return
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.obtenerSugerencias(userId)
                if (response.isSuccessful) {
                    val suggestions: List<Habito> = response.body() ?: emptyList()
                    adapter.updateData(suggestions)
                } else {
                    Toast.makeText(this@SuggestionsActivity, "Error al cargar sugerencias.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SuggestionsActivity, "Error de red.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addSuggestedHabit(habit: Habito) {
        val userId = SharedPrefManager.getInstance(this).getUserId() ?: return
        lifecycleScope.launch {
            try {
                // Crear y vincular el hábito sugerido
                RetrofitClient.apiService.crearHabito(habit)
                RetrofitClient.apiService.vincularHabito(mapOf("userId" to userId, "habitId" to habit.id))
                Toast.makeText(this@SuggestionsActivity, "Hábito agregado: ${habit.nombre}", Toast.LENGTH_SHORT).show()
                loadSuggestions() // recargar en caso de que ya no deba volver a sugerirse
            } catch (e: Exception) {
                Toast.makeText(this@SuggestionsActivity, "Error al agregar hábito", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
