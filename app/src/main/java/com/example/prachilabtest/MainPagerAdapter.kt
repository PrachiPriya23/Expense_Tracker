package com.example.prachilabtest

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    val expensesListFragment = ExpensesListFragment()
    val addExpenseFragment = AddExpenseFragment()
    val summaryFragment = SummaryFragment()

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> expensesListFragment
            1 -> addExpenseFragment
            2 -> summaryFragment
            else -> expensesListFragment
        }
    }
}
