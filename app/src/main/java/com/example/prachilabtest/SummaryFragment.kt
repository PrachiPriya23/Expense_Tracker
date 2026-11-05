package com.example.prachilabtest

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.util.Locale

class SummaryFragment : Fragment() {

    private lateinit var tvSummaryTotalSpent: TextView
    private lateinit var tvSummaryBudget: TextView
    private lateinit var tvBudgetStatusChip: TextView
    private lateinit var progressSummaryBudget: LinearProgressIndicator

    private lateinit var tvFoodTotal: TextView
    private lateinit var progressFood: ProgressBar

    private lateinit var tvTravelTotal: TextView
    private lateinit var progressTravel: ProgressBar

    private lateinit var tvStayTotal: TextView
    private lateinit var progressStay: ProgressBar

    private lateinit var tvShoppingTotal: TextView
    private lateinit var progressShopping: ProgressBar

    private lateinit var dbHelper: ExpenseDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.tab_summary, container, false)

        dbHelper = ExpenseDatabaseHelper(requireContext())

        tvSummaryTotalSpent = view.findViewById(R.id.tvSummaryTotalSpent)
        tvSummaryBudget = view.findViewById(R.id.tvSummaryBudget)
        tvBudgetStatusChip = view.findViewById(R.id.tvBudgetStatusChip)
        progressSummaryBudget = view.findViewById(R.id.progressSummaryBudget)

        tvFoodTotal = view.findViewById(R.id.tvFoodTotal)
        progressFood = view.findViewById(R.id.progressFood)

        tvTravelTotal = view.findViewById(R.id.tvTravelTotal)
        progressTravel = view.findViewById(R.id.progressTravel)

        tvStayTotal = view.findViewById(R.id.tvStayTotal)
        progressStay = view.findViewById(R.id.progressStay)

        tvShoppingTotal = view.findViewById(R.id.tvShoppingTotal)
        progressShopping = view.findViewById(R.id.progressShopping)

        refreshSummary()

        return view
    }

    fun refreshSummary() {
        if (!isAdded) return

        val totalSpent = dbHelper.getTotalSpent()
        val prefs = requireContext().getSharedPreferences("TripExpensePrefs", Context.MODE_PRIVATE)
        val budget = prefs.getFloat("trip_budget", 5000.0f).toDouble()

        tvSummaryTotalSpent.text = String.format(Locale.US, "₹%.2f", totalSpent)
        tvSummaryBudget.text = String.format(Locale.US, "₹%.2f", budget)

        // Progress percentage vs Budget
        val budgetProgressPercent = if (budget > 0) {
            ((totalSpent / budget) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }
        progressSummaryBudget.progress = budgetProgressPercent

        if (totalSpent > budget) {
            tvBudgetStatusChip.text = "Budget Exceeded!"
            tvBudgetStatusChip.setTextColor(ContextCompat.getColor(requireContext(), R.color.alert_red))
            tvBudgetStatusChip.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.alert_bg)
            progressSummaryBudget.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.alert_red))
        } else {
            tvBudgetStatusChip.text = "Within Budget"
            tvBudgetStatusChip.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_green))
            tvBudgetStatusChip.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.success_bg)
            progressSummaryBudget.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.primary))
        }

        // Category Totals using SQL SUM with GROUP BY
        val categoryTotals = dbHelper.getCategoryTotals()

        val foodTotal = categoryTotals["Food"] ?: 0.0
        val travelTotal = categoryTotals["Travel"] ?: 0.0
        val stayTotal = categoryTotals["Stay"] ?: 0.0
        val shoppingTotal = categoryTotals["Shopping"] ?: 0.0

        tvFoodTotal.text = String.format(Locale.US, "₹%.2f", foodTotal)
        tvTravelTotal.text = String.format(Locale.US, "₹%.2f", travelTotal)
        tvStayTotal.text = String.format(Locale.US, "₹%.2f", stayTotal)
        tvShoppingTotal.text = String.format(Locale.US, "₹%.2f", shoppingTotal)

        // Calculate percentage of category over total spent (or 0 if total is 0)
        progressFood.progress = if (totalSpent > 0) ((foodTotal / totalSpent) * 100).toInt() else 0
        progressTravel.progress = if (totalSpent > 0) ((travelTotal / totalSpent) * 100).toInt() else 0
        progressStay.progress = if (totalSpent > 0) ((stayTotal / totalSpent) * 100).toInt() else 0
        progressShopping.progress = if (totalSpent > 0) ((shoppingTotal / totalSpent) * 100).toInt() else 0
    }

    override fun onResume() {
        super.onResume()
        refreshSummary()
    }
}
