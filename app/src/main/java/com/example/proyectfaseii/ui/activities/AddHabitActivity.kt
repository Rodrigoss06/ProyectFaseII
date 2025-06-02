package com.example.proyectfaseii.ui.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectfaseii.R
import com.example.proyectfaseii.data.api.RetrofitClient
import com.example.proyectfaseii.data.models.Habito
import com.example.proyectfaseii.utils.SharedPrefManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Permite crear un nuevo hábito (nombre + categoría) y vincularlo al usuario.
 */
class AddHabitActivity : AppCompatActivity() {

    private lateinit var etHabitName: EditText
    private lateinit var etHabitCategory: EditText
    private lateinit var btnSaveHabit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_habit)

        etHabitName = findViewById(R.id.etHabitName)
        etHabitCategory = findViewById(R.id.etHabitCategory)
        btnSaveHabit = findViewById(R.id.btnSaveHabit)

        btnSaveHabit.setOnClickListener {
            val name = etHabitName.text.toString().trim()
            val category = etHabitCategory.text.toString().trim()
            if (name.isEmpty()) {
                etHabitName.error = "Ingresa un nombre"
                return@setOnClickListener
            }
            if (category.isEmpty()) {
                etHabitCategory.error = "Ingresa una categoría"
                return@setOnClickListener
            }
            saveHabit(name, category)
        }
    }

    private fun saveHabit(name: String, category: String) {
        val habitId = java.util.UUID.randomUUID().toString()
        val habito = Habito(id = habitId, nombre = name, categoria = category)
        val userId = SharedPrefManager.getInstance(this).getUserId() ?: return

        // 1) Crear hábito en backend
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response1 = RetrofitClient.apiService.crearHabito(habito)
                if (response1.isSuccessful) {
                    // 2) Vincular hábito al usuario
                    val linkData = mapOf("userId" to userId, "habitId" to habitId)
                    val response2 = RetrofitClient.apiService.vincularHabito(linkData)
                    if (response2.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@AddHabitActivity, "Hábito agregado", Toast.LENGTH_SHORT).show()
                            finish() // Regresa a HomeActivity
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@AddHabitActivity, "Error al vincular hábito", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@AddHabitActivity, "Error al crear hábito", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@AddHabitActivity, "Error de red", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
