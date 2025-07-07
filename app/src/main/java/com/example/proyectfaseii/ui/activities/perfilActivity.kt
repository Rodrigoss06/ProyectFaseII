package com.example.proyectfaseii.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectfaseii.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PerfilActivity : AppCompatActivity() {

    private lateinit var btnEditarPerfil: Button
    private lateinit var txtNombre: TextView
    private lateinit var txtDescripcion: TextView
    private lateinit var txtCorreo: TextView
    private lateinit var imgPerfil: ImageView
    private lateinit var btnEditarFoto: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        // Referenciar vistas
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil)
        txtNombre = findViewById(R.id.txtNombre)
        txtDescripcion = findViewById(R.id.txtDescripcion)
        txtCorreo = findViewById(R.id.txtCorreo)
        imgPerfil = findViewById(R.id.imgPerfil)
        btnEditarFoto = findViewById(R.id.btnEditarFoto)

        // Simulación de carga inicial de datos
        txtNombre.text = "Alexandra Alegre"
        txtCorreo.text = "alexandra@correo.com"
        txtDescripcion.text = "Desarrolladora Android entusiasta."

        // Abrir actividad para editar perfil
        btnEditarPerfil.setOnClickListener {
            val intent = Intent(this, EditarPerfilActivity::class.java)
            intent.putExtra("nombre", txtNombre.text.toString())
            intent.putExtra("descripcion", txtDescripcion.text.toString())
            startActivityForResult(intent, 100)
        }

        // Opción para editar la foto (placeholder)
        btnEditarFoto.setOnClickListener {
            Toast.makeText(this, "Función para cambiar foto aún no implementada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            val nuevoNombre = data.getStringExtra("nombreActualizado")
            val nuevaDescripcion = data.getStringExtra("descripcionActualizada")

            txtNombre.text = nuevoNombre
            txtDescripcion.text = nuevaDescripcion
        }
    }
}
