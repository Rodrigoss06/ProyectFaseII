package com.example.proyectfaseii.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectfaseii.R
import com.example.proyectfaseii.data.api.RetrofitClient
import com.example.proyectfaseii.data.models.Usuario
import com.example.proyectfaseii.utils.SharedPrefManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Pantalla de registro de usuario con Firebase Auth y backend Neo4j
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvGoToLogin: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvGoToLogin = findViewById(R.id.tvGoToLogin)

        btnRegister.setOnClickListener {
            val nombre = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if (nombre.isEmpty()) {
                etName.error = "Ingresa tu nombre"
                return@setOnClickListener
            }
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Correo inválido"
                return@setOnClickListener
            }
            if (password.length < 6) {
                etPassword.error = "La contraseña debe tener al menos 6 caracteres"
                return@setOnClickListener
            }
            registerUser(nombre, email, password)
        }

        tvGoToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser(nombre: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    firebaseUser?.let { user ->
                        // Actualizar displayName en Firebase User
                        val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                            .setDisplayName(nombre)
                            .build()
                        user.updateProfile(profileUpdates).addOnCompleteListener {
                            // Guardar en SharedPref
                            SharedPrefManager.getInstance(this)
                                .saveUser(user.uid, nombre, user.email ?: email)

                            // Crear usuario en backend
                            val usuario = Usuario(id = user.uid, nombre = nombre, email = user.email ?: email)
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    RetrofitClient.apiService.crearUsuario(usuario)
                                } catch (_: Exception) {
                                    // Ignorar errores
                                }
                            }

                            // Ir a pantalla de Login
                            Toast.makeText(this, "Registro exitoso. Por favor, inicia sesión.", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
