package com.example.proyectfaseii.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.proyectfaseii.R
import com.example.proyectfaseii.ui.adapters.MainPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        viewPager.adapter = MainPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Today"
                    tab.setIcon(R.drawable.ic_today)
                }
                1 -> {
                    tab.text = "Upcoming"
                    tab.setIcon(R.drawable.ic_calendar)
                }
                2 -> {
                    tab.text = "Stats"
                    tab.setIcon(R.drawable.ic_stats)
                }
            }
        }.attach()

    }
}
