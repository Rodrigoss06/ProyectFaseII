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
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Pantalla de inicio de sesión con Firebase Auth y guarda usuario en backend/Neo4j
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvGoToRegister: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            FirebaseApp.getInstance()
        } catch (e: IllegalStateException) {
            FirebaseApp.initializeApp(this)
        }

        setContentView(R.layout.activity_login)

        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance()

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvGoToRegister = findViewById(R.id.tvGoToRegister)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Correo inválido"
                return@setOnClickListener
            }
            if (password.length < 6) {
                etPassword.error = "La contraseña debe tener al menos 6 caracteres"
                return@setOnClickListener
            }
            loginUser(email, password)
        }

        tvGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Obtener usuario de Firebase
                    val firebaseUser = auth.currentUser
                    firebaseUser?.let { user ->
                        // Guardar en SharedPref
                        SharedPrefManager.getInstance(this)
                            .saveUser(user.uid, user.displayName ?: "Usuario", user.email ?: email)

                        // También crear/actualizar en backend (Neo4j)
                        val usuario = Usuario(id = user.uid, nombre = user.displayName ?: "Usuario", email = user.email ?: email)
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                RetrofitClient.apiService.crearUsuario(usuario)
                            } catch (_: Exception) {
                                // Ignorar errores de red
                            }
                        }

                        // Ir a HomeActivity
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(this, "Error de autenticación: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
