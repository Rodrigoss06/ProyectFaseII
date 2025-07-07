package com.example.proyectfaseii.data.firebase

import android.widget.Toast
import com.example.proyectfaseii.data.models.Habito
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreManager {
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid



    fun obtenerHabitos(onSuccess: (List<Habito>) -> Unit, onFailure: (Exception) -> Unit) {
        if (userId == null) return

        db.collection("usuarios").document(userId).collection("habitos")
            .get()
            .addOnSuccessListener { result ->
                val lista = result.mapNotNull { it.toObject(Habito::class.java) }
                onSuccess(lista)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun addHabit(userId: String, habit: Habito, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        db.collection("users")
            .document(userId)
            .collection("habits")
            .document(habit.id)
            .set(habit)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    // Otros métodos: eliminarHabito(), actualizarHabito(), etc.
}
