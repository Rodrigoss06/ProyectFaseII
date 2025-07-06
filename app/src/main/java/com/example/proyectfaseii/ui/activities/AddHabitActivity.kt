package com.example.proyectfaseii.ui.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.proyectfaseii.R
import com.example.proyectfaseii.data.api.RetrofitClient
import com.example.proyectfaseii.data.firebase.FirestoreManager
import com.example.proyectfaseii.data.models.*
import com.example.proyectfaseii.ml.TextScanner
import com.example.proyectfaseii.utils.SharedPrefManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*

class AddHabitActivity : AppCompatActivity() {

    private lateinit var etHabitName: EditText
    private lateinit var etRecurrence: EditText
    private lateinit var etArea: EditText
    private lateinit var etGoalValue: EditText
    private lateinit var etGoalUnitType: EditText
    private lateinit var etGoalPeriodicity: EditText
    private lateinit var btnScanText: Button
    private lateinit var btnSaveHabit: Button

    private val REQUEST_STORAGE_PERMISSION = 1001

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_habit)

        etHabitName = findViewById(R.id.etHabitName)
        etRecurrence = findViewById(R.id.etRecurrence)
        etArea = findViewById(R.id.etArea)
        etGoalValue = findViewById(R.id.etGoalValue)
        etGoalUnitType = findViewById(R.id.etGoalUnitType)
        etGoalPeriodicity = findViewById(R.id.etGoalPeriodicity)
        btnScanText = findViewById(R.id.btnScanText)
        btnSaveHabit = findViewById(R.id.btnSaveHabit)

        btnScanText.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_STORAGE_PERMISSION
                )
            } else {
                openImagePicker()
            }
        }

        btnSaveHabit.setOnClickListener {
            val name = etHabitName.text.toString().trim()
            val recurrence = etRecurrence.text.toString().trim()
            val areaName = etArea.text.toString().trim()
            val goal = Goal(
                value = etGoalValue.text.toString().toDoubleOrNull() ?: 0.0,
                unit_type = etGoalUnitType.text.toString().trim(),
                periodicity = etGoalPeriodicity.text.toString().trim()
            )

            if (name.isEmpty()) {
                etHabitName.error = "Enter a name"
                return@setOnClickListener
            }

            val habit = Habito(
                id = UUID.randomUUID().toString(),
                name = name,
                is_archived = false,
                start_date = LocalDate.now().toString(),
                time_of_day = emptyList(),
                goal = goal,
                goal_history_items = emptyList(),
                log_method = "manual",
                recurrence = recurrence,
                remind = listOf(),
                area = if (areaName.isNotBlank()) Area(name = areaName) else null,
                created_date = LocalDate.now().toString(),
                priority = 1.0
            )

            saveHabitToBackend(habit)
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val imageUri: Uri? = result.data!!.data
            try {
                val inputStream = contentResolver.openInputStream(imageUri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                TextScanner.scanFromBitmap(bitmap,
                    onSuccess = { scannedText ->
                        etHabitName.setText(scannedText)
                    },
                    onError = { e ->
                        Toast.makeText(this, "Text recognition failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    })

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Image load failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_STORAGE_PERMISSION &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            openImagePicker()
        } else {
            Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveHabitToBackend(habit: Habito) {
        val userId = SharedPrefManager.getInstance(this).getUserId()
        if (userId == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        FirestoreManager.addHabit(
            userId = userId,
            habit = habit,
            onSuccess = {
                runOnUiThread {
                    Toast.makeText(this, "Hábito creado en Firestore", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            onError = { e ->
                Log.e("FirestoreManager", "Error al guardar hábito", e)
                runOnUiThread {
                    Toast.makeText(this, "Error al guardar hábito: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

        )
    }
}
