package com.example.proyectfaseii.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.proyectfaseii.R
import com.example.proyectfaseii.data.api.RetrofitClient
import com.example.proyectfaseii.data.models.Habito
import com.example.proyectfaseii.ui.activities.AddHabitActivity
import com.example.proyectfaseii.ui.activities.HabitDetailActivity
import com.example.proyectfaseii.ui.adapters.HabitosAdapter
import com.example.proyectfaseii.utils.SharedPrefManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class TodayFragment : Fragment() {

    private lateinit var tvGreeting: TextView
    private lateinit var tvCompletedCount: TextView
    private lateinit var tvPendingCount: TextView
    private lateinit var rvHabitos: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var fabAddHabit: FloatingActionButton
    private lateinit var adapter: HabitosAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_today, container, false)

        tvGreeting = view.findViewById(R.id.tvGreeting)
        tvCompletedCount = view.findViewById(R.id.tvCompletedCount)
        tvPendingCount = view.findViewById(R.id.tvPendingCount)
        rvHabitos = view.findViewById(R.id.rvHabitos)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        fabAddHabit = view.findViewById(R.id.fabAddHabit)

        val prefs = SharedPrefManager.getInstance(requireContext())
        val userName = prefs.getUserName() ?: "Usuario"
        tvGreeting.text = getString(R.string.greeting, userName)

        adapter = HabitosAdapter(emptyList()) { habit ->
            val intent = Intent(requireContext(), HabitDetailActivity::class.java)
            intent.putExtra("habit_id", habit.id)
            intent.putExtra("habit_name", habit.name)
            intent.putExtra("habit_category", habit.area?.name ?: "")
            startActivity(intent)
        }

        rvHabitos.layoutManager = LinearLayoutManager(requireContext())
        rvHabitos.adapter = adapter

        swipeRefresh.setOnRefreshListener {
            loadHabits()
        }

        fabAddHabit.setOnClickListener {
            startActivity(Intent(requireContext(), AddHabitActivity::class.java))
        }

        loadHabits()

        return view
    }

    private fun loadHabits() {
        val userId = SharedPrefManager.getInstance(requireContext()).getUserId()
        if (userId == null) {
            Toast.makeText(context, "Usuario no identificado", Toast.LENGTH_LONG).show()
            return
        }

        swipeRefresh.isRefreshing = true

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.obtenerHabitos(userId)
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    adapter.updateData(list)

                    tvCompletedCount.text = "Completados: ${list.count { it.current_streak > 0 }}"
                    tvPendingCount.text = "Pendientes: ${list.count { it.current_streak == 0 }}"
                } else {
                    Toast.makeText(context, "Error al obtener hábitos", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
            } finally {
                swipeRefresh.isRefreshing = false
            }
        }
    }
}
