// Note.kt
package com.example.proyectfaseii.data.models

data class Note(
    val id: String = "",
    val content: String = "",
    val created_date: String = "",
    val image_url: String? = null,
    val note_type: Int = 0,
    val habit_id: String = ""
)
