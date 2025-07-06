package com.example.proyectfaseii.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.proyectfaseii.ui.fragments.UpcomingFragment
import com.example.proyectfaseii.ui.fragments.StatsFragment
import com.example.proyectfaseii.ui.fragments.TodayFragment

class MainPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TodayFragment()
            1 -> UpcomingFragment()
            2 -> StatsFragment()
            else -> TodayFragment()
        }
    }
}
