package com.example.proyectfaseii.ui.activities

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectfaseii.R
import com.example.proyectfaseii.data.models.Habito
import com.example.proyectfaseii.utils.SharedPrefManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.time.LocalDate
import java.util.*
import java.util.concurrent.TimeUnit

class HabitDetailActivity : AppCompatActivity() {

    private lateinit var tvHabitName: TextView
    private lateinit var tvHabitCategory: TextView
    private lateinit var btnMarkComplete: Button
    private lateinit var konfettiView: KonfettiView
    private lateinit var firestore: FirebaseFirestore

    private var habitId: String? = null
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_detail)

        tvHabitName = findViewById(R.id.tvHabitName)
        tvHabitCategory = findViewById(R.id.tvHabitCategory)
        btnMarkComplete = findViewById(R.id.btnMarkComplete)
        konfettiView = findViewById(R.id.konfettiView)

        firestore = FirebaseFirestore.getInstance()
        userId = SharedPrefManager.getInstance(this).getUserId()

        habitId = intent.getStringExtra("habit_id")
        val habitName = intent.getStringExtra("habit_name")
        val habitCategory = intent.getStringExtra("habit_category")

        tvHabitName.text = habitName
        tvHabitCategory.text = habitCategory

        btnMarkComplete.setOnClickListener {
            if (habitId != null && userId != null) {
                markHabitCompletedWithFirestore(userId!!, habitId!!)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun markHabitCompletedWithFirestore(userId: String, habitId: String) {
        val docRef = firestore.collection("users").document(userId)
            .collection("habits").document(habitId)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val snapshot = docRef.get().await()
                val habit = snapshot.toObject(Habito::class.java)

                if (habit != null) {
                    val today = LocalDate.now()
                    val lastDate = habit.last_completed_date.takeIf { it.isNotEmpty() }?.let {
                        LocalDate.parse(it)
                    }

                    val newStreak = if (lastDate != null && lastDate.plusDays(1) == today) {
                        habit.current_streak + 1
                    } else {
                        1
                    }

                    val updatedData = mapOf(
                        "current_streak" to newStreak,
                        "longest_streak" to maxOf(habit.longest_streak, newStreak),
                        "last_completed_date" to today.toString()
                    )

                    docRef.update(updatedData).await()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@HabitDetailActivity, "¡Completado en Firestore! 🎉", Toast.LENGTH_SHORT).show()
                        showConfetti()
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@HabitDetailActivity, "No se encontró el hábito", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@HabitDetailActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showConfetti() {
        val party = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(Color.YELLOW, Color.GREEN, Color.MAGENTA),
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).perSecond(80),
            position = Position.Relative(0.5, 0.5)
        )
        konfettiView.start(party)
    }
}
