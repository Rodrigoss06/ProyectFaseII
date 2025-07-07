package com.example.proyectfaseii.ui.activities

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectfaseii.R

class EditarPerfilActivity : AppCompatActivity() {

    private lateinit var edtNombre: EditText
    private lateinit var edtDescripcion: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        edtNombre = findViewById(R.id.edtNombre)
        edtDescripcion = findViewById(R.id.edtDescripcion)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnCancelar = findViewById(R.id.btnCancelar)

        // Cargar datos recibidos
        edtNombre.setText(intent.getStringExtra("nombre"))
        edtDescripcion.setText(intent.getStringExtra("descripcion"))

        btnGuardar.setOnClickListener {
            val intentResult = intent
            intentResult.putExtra("nombreActualizado", edtNombre.text.toString())
            intentResult.putExtra("descripcionActualizada", edtDescripcion.text.toString())
            setResult(Activity.RESULT_OK, intentResult)
            finish()
        }

        btnCancelar.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}
