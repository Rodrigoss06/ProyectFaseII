// Habito.kt
package com.example.proyectfaseii.data.models

data class Habito(
    val id: String = "",
    val name: String = "",
    val is_archived: Boolean = false,
    val start_date: String = "",
    val time_of_day: List<String> = emptyList(),
    val goal: Goal = Goal(),
    val goal_history_items: List<Goal> = emptyList(),
    val log_method: String = "",
    val recurrence: String = "",
    val remind: List<String> = emptyList(),
    val area: Area? = null,
    val created_date: String = "",
    val priority: Double = 0.0,

    // NEW
    val current_streak: Int = 0,
    val longest_streak: Int = 0,
    val last_completed_date: String = ""
)
