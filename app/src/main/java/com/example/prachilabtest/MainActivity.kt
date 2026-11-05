package com.example.prachilabtest

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputEditText
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var tvHeaderSubBudget: TextView
    private lateinit var btnSetBudget: MaterialButton
    private lateinit var llBudgetWarningBanner: LinearLayout
    private lateinit var tvBudgetWarningText: TextView

    private lateinit var pagerAdapter: MainPagerAdapter
    private lateinit var dbHelper: ExpenseDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = ExpenseDatabaseHelper(this)

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        tvHeaderSubBudget = findViewById(R.id.tvHeaderSubBudget)
        btnSetBudget = findViewById(R.id.btnSetBudget)
        llBudgetWarningBanner = findViewById(R.id.llBudgetWarningBanner)
        tvBudgetWarningText = findViewById(R.id.tvBudgetWarningText)

        pagerAdapter = MainPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        val tabTitles = arrayOf("Expenses", "Add Expense", "Summary")
        val tabIcons = arrayOf(
            R.drawable.ic_receipt_placeholder,
            R.drawable.ic_attach,
            R.drawable.ic_budget
        )

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
            tab.setIcon(tabIcons[position])
        }.attach()

        btnSetBudget.setOnClickListener {
            showSetBudgetDialog()
        }

        refreshAllData()
    }

    fun switchToTab(position: Int) {
        if (position in 0..2) {
            viewPager.currentItem = position
        }
    }

    fun refreshAllData() {
        pagerAdapter.expensesListFragment.refreshExpenses()
        pagerAdapter.summaryFragment.refreshSummary()
        checkBudgetAlert()
    }

    private fun checkBudgetAlert() {
        val totalSpent = dbHelper.getTotalSpent()
        val prefs = getSharedPreferences("TripExpensePrefs", MODE_PRIVATE)
        val budget = prefs.getFloat("trip_budget", 5000.0f).toDouble()

        tvHeaderSubBudget.text = String.format(
            Locale.US,
            "Spent: ₹%.2f / Budget: ₹%.2f",
            totalSpent,
            budget
        )

        if (totalSpent > budget) {
            val exceedAmount = totalSpent - budget
            llBudgetWarningBanner.visibility = View.VISIBLE
            tvBudgetWarningText.text = String.format(
                Locale.US,
                "⚠️ Budget Exceeded by ₹%.2f! (Spent ₹%.2f / Budget ₹%.2f)",
                exceedAmount,
                totalSpent,
                budget
            )
            Toast.makeText(
                this,
                String.format(
                    Locale.US,
                    "⚠️ Budget alert! Total spent (₹%.2f) exceeds trip budget (₹%.2f)!",
                    totalSpent,
                    budget
                ),
                Toast.LENGTH_LONG
            ).show()
        } else {
            llBudgetWarningBanner.visibility = View.GONE
        }
    }

    private fun showSetBudgetDialog() {
        val dialog = Dialog(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_set_budget, null)
        dialog.setContentView(dialogView)

        val etBudgetAmount: TextInputEditText = dialogView.findViewById(R.id.etBudgetAmount)
        val prefs = getSharedPreferences("TripExpensePrefs", MODE_PRIVATE)
        val currentBudget = prefs.getFloat("trip_budget", 5000.0f)
        etBudgetAmount.setText(String.format(Locale.US, "%.2f", currentBudget))

        val btnCancel = Button(this).apply {
            text = "Cancel"
        }
        val btnSave = Button(this).apply {
            text = "Save"
        }

        val buttonContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END
            setPadding(0, 16, 0, 0)
            addView(btnCancel)
            addView(btnSave)
        }
        (dialogView as ViewGroup).addView(buttonContainer)

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            val input = etBudgetAmount.text?.toString()?.trim() ?: ""
            val newBudget = input.toFloatOrNull()
            if (newBudget == null || newBudget <= 0) {
                etBudgetAmount.error = "Enter a valid positive budget"
            } else {
                prefs.edit().putFloat("trip_budget", newBudget).apply()
                Toast.makeText(this, "Budget updated to ₹${String.format(Locale.US, "%.2f", newBudget)}", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                refreshAllData()
            }
        }

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
    }
}
