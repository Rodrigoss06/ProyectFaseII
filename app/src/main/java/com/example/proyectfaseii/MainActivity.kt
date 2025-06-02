package com.example.proyectfaseii

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.example.proyectfaseii.ui.activities.LoginActivity

/**
 * Activity de arranque que inicializa Firebase y redirige a LoginActivity
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializa Firebase
        FirebaseApp.initializeApp(this)
        // Lanzar pantalla de Login
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
