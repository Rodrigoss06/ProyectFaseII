package com.example.proyectfaseii.ui.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectfaseii.R
import com.example.proyectfaseii.data.api.RetrofitClient
import com.example.proyectfaseii.data.models.Habito
import com.example.proyectfaseii.ui.adapters.HabitosAdapter
import com.example.proyectfaseii.utils.SharedPrefManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvGreeting: TextView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var rvHabitos: RecyclerView
    private lateinit var adapter: HabitosAdapter
    private lateinit var fabAddHabit: FloatingActionButton
    private val REQUEST_ALL_PERMISSIONS = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (!hasRequiredPermissions()) {
            requestRequiredPermissions()
        }

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        tvGreeting = findViewById(R.id.tvGreeting)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        rvHabitos = findViewById(R.id.rvHabitos)
        fabAddHabit = findViewById(R.id.fabAddHabit)

        // Mostrar saludo personalizado
        val prefs = SharedPrefManager.getInstance(this)
        val userName = prefs.getUserName() ?: "Usuario"
        tvGreeting.text = getString(R.string.greeting, userName)

        // Configurar RecyclerView
        adapter = HabitosAdapter(emptyList()) { habit ->
            // Al tocar un hábito, abrir detalle
            val intent = Intent(this, HabitDetailActivity::class.java)
            intent.putExtra("habit_id", habit.id)
            intent.putExtra("habit_name", habit.name)
            intent.putExtra("habit_category", habit.area?.name ?: "")
            startActivity(intent)
        }
        rvHabitos.layoutManager = LinearLayoutManager(this)
        rvHabitos.adapter = adapter

        // Swipe to refresh
        swipeRefresh.setOnRefreshListener {
            loadHabits()
        }

        // Cargar hábitos al inicio
        loadHabits()

        // FloatingActionButton abre AddHabitActivity
        fabAddHabit.setOnClickListener {
            val intent = Intent(this, AddHabitActivity::class.java)
            startActivity(intent)
        }
        val picker = MaterialDatePicker.Builder.datePicker().build()
        picker.show(supportFragmentManager, picker.toString())

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_ALL_PERMISSIONS) {
            val denied = permissions.zip(grantResults.toTypedArray())
                .filter { it.second != PackageManager.PERMISSION_GRANTED }
                .map { it.first }

            if (denied.isEmpty()) {
                Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permisos denegados: ${denied.joinToString()}", Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        // Volver a cargar cuando regrese de AddHabit
        loadHabits()
    }

    private fun loadHabits() {
        val userId = SharedPrefManager.getInstance(this).getUserId()
        if (userId == null) {
            Toast.makeText(this, "Usuario no identificado.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        swipeRefresh.isRefreshing = true
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.obtenerHabitos(userId)
                if (response.isSuccessful) {
                    val listaHabitos: List<Habito> = response.body() ?: emptyList()
                    adapter.updateData(listaHabitos)
                } else {
                    Toast.makeText(this@HomeActivity, "Error al obtener hábitos.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@HomeActivity, "Error de red.", Toast.LENGTH_SHORT).show()
            } finally {
                swipeRefresh.isRefreshing = false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_logout -> {
                SharedPrefManager.getInstance(this).clear()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            R.id.action_suggestions -> {
                startActivity(Intent(this, SuggestionsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun hasRequiredPermissions(): Boolean {
        val permissionsToCheck = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToCheck.add(android.Manifest.permission.POST_NOTIFICATIONS)
            permissionsToCheck.add(android.Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissionsToCheck.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        return permissionsToCheck.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }


    private fun requestRequiredPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(android.Manifest.permission.POST_NOTIFICATIONS)
            permissionsToRequest.add(android.Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissionsToRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), REQUEST_ALL_PERMISSIONS)
    }


}