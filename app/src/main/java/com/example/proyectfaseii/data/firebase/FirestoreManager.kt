package com.example.proyectfaseii.data.firebase

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

    fun agregarHabito(habito: Habito, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        if (userId == null) return

        db.collection("usuarios").document(userId).collection("habitos")
            .add(habito)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    // Otros métodos: eliminarHabito(), actualizarHabito(), etc.
}
