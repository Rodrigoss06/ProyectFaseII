// Status.kt
package com.example.proyectfaseii.data.models

data class Status(
    val status: String = "",
    val progress: Progress = Progress()
)

data class Progress(
    val current_value: Double = 0.0,
    val target_value: Double = 0.0,
    val unit_type: String = "",
    val periodicity: String = "",
    val reference_date: String = ""
)
