package com.example.proyectfaseii.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectfaseii.R
import com.example.proyectfaseii.utils.SharedPrefManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class PerfilActivity : AppCompatActivity() {

    private lateinit var btnEditarPerfil: Button
    private lateinit var btnCerrarSesion: Button
    private lateinit var txtNombre: TextView
    private lateinit var txtDescripcion: TextView
    private lateinit var txtCorreo: TextView
    private lateinit var imgPerfil: ImageView
    private lateinit var btnEditarFoto: FloatingActionButton

    private lateinit var firestore: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var editarPerfilLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        // Inicialización
        firestore = FirebaseFirestore.getInstance()
        val sharedPrefs = SharedPrefManager.getInstance(this)
        userId = sharedPrefs.getUserId() ?: return

        // Referencias UI
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)
        txtNombre = findViewById(R.id.txtNombre)
        txtDescripcion = findViewById(R.id.txtDescripcion)
        txtCorreo = findViewById(R.id.txtCorreo)
        imgPerfil = findViewById(R.id.imgPerfil)
        btnEditarFoto = findViewById(R.id.btnEditarFoto)

        txtCorreo.text = sharedPrefs.getUserEmail()
        cargarPerfilDesdeFirestore()

        // Result launcher moderno
        editarPerfilLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data ?: return@registerForActivityResult
                val nuevoNombre = data.getStringExtra("nombreActualizado") ?: return@registerForActivityResult
                val nuevaDescripcion = data.getStringExtra("descripcionActualizada") ?: ""

                txtNombre.text = nuevoNombre
                txtDescripcion.text = nuevaDescripcion

                val updates = mapOf(
                    "nombre" to nuevoNombre,
                    "descripcion" to nuevaDescripcion
                )

                firestore.collection("users").document(userId)
                    .update(updates)
                    .addOnSuccessListener {
                        sharedPrefs.saveUser(
                            id = userId,
                            nombre = nuevoNombre,
                            email = sharedPrefs.getUserEmail() ?: "",
                            descripcion = nuevaDescripcion
                        )
                        Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al actualizar: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        btnEditarPerfil.setOnClickListener {
            val intent = Intent(this, EditarPerfilActivity::class.java).apply {
                putExtra("nombre", txtNombre.text.toString())
                putExtra("descripcion", txtDescripcion.text.toString())
            }
            editarPerfilLauncher.launch(intent)
        }

        btnEditarFoto.setOnClickListener {
            Toast.makeText(this, "Función de cambiar foto aún no implementada", Toast.LENGTH_SHORT).show()
        }

        btnCerrarSesion.setOnClickListener {
            sharedPrefs.clear()
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }
    }

    private fun cargarPerfilDesdeFirestore() {
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    txtNombre.text = doc.getString("nombre") ?: "Sin nombre"
                    txtDescripcion.text = doc.getString("descripcion") ?: "Sin descripción"
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar perfil: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
