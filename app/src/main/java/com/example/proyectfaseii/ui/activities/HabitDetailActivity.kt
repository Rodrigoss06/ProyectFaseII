package com.example.proyectfaseii.ui.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectfaseii.R
import com.example.proyectfaseii.utils.SharedPrefManager
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.*

/**
 * Muestra detalles de un hábito (nombre, categoría, y permite marcarlo como completado).
 */
class HabitDetailActivity : AppCompatActivity() {

    private lateinit var tvHabitName: TextView
    private lateinit var tvHabitCategory: TextView
    private lateinit var btnMarkComplete: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_detail)

        tvHabitName = findViewById(R.id.tvHabitName)
        tvHabitCategory = findViewById(R.id.tvHabitCategory)
        btnMarkComplete = findViewById(R.id.btnMarkComplete)

        // Leer datos pasados desde HomeActivity
        val habitId = intent.getStringExtra("habit_id")
        val habitName = intent.getStringExtra("habit_name")
        val habitCategory = intent.getStringExtra("habit_category")

        tvHabitName.text = habitName
        tvHabitCategory.text = habitCategory

        btnMarkComplete.setOnClickListener {
            markHabitCompleted(habitId ?: return@setOnClickListener)
        }
    }

    private fun markHabitCompleted(habitId: String) {
        val userId = SharedPrefManager.getInstance(this).getUserId() ?: return
        // Guardar marca de completado en Firebase Realtime Database
        val database = FirebaseDatabase.getInstance().getReference("completed")
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.format(Date())
        val key = "$userId-$habitId-$today"

        val data = mapOf(
            "userId" to userId,
            "habitId" to habitId,
            "date" to today
        )

        database.child(key).setValue(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Hábito marcado como completado", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al marcar completado", Toast.LENGTH_SHORT).show()
            }
    }
}
