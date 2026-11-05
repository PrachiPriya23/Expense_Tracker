package com.example.prachilabtest

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import java.util.Locale

class ExpensesListFragment : Fragment() {

    private lateinit var lvExpenses: ListView
    private lateinit var llEmptyState: LinearLayout
    private lateinit var fabAddExpense: ExtendedFloatingActionButton
    private lateinit var dbHelper: ExpenseDatabaseHelper

    private var adapter: ExpenseAdapter? = null
    private val expenseList = mutableListOf<Expense>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.tab_expenses, container, false)

        dbHelper = ExpenseDatabaseHelper(requireContext())
        lvExpenses = view.findViewById(R.id.lvExpenses)
        llEmptyState = view.findViewById(R.id.llEmptyState)
        fabAddExpense = view.findViewById(R.id.fabAddExpense)

        adapter = ExpenseAdapter(requireContext(), expenseList)
        lvExpenses.adapter = adapter

        // Short click: view full screenshot dialog
        lvExpenses.setOnItemClickListener { _, _, position, _ ->
            if (position in expenseList.indices) {
                showFullScreenshotDialog(expenseList[position])
            }
        }

        // Long press: delete expense
        lvExpenses.setOnItemLongClickListener { _, _, position, _ ->
            if (position in expenseList.indices) {
                confirmDeleteExpense(expenseList[position])
            }
            true
        }

        fabAddExpense.setOnClickListener {
            (activity as? MainActivity)?.switchToTab(1)
        }

        refreshExpenses()

        return view
    }

    fun refreshExpenses() {
        if (!isAdded) return
        expenseList.clear()
        expenseList.addAll(dbHelper.getAllExpenses())
        adapter?.notifyDataSetChanged()

        if (expenseList.isEmpty()) {
            llEmptyState.visibility = View.VISIBLE
            lvExpenses.visibility = View.GONE
        } else {
            llEmptyState.visibility = View.GONE
            lvExpenses.visibility = View.VISIBLE
        }
    }

    private fun showFullScreenshotDialog(expense: Expense) {
        val dialog = Dialog(requireContext())
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_full_screenshot, null)
        dialog.setContentView(dialogView)

        val tvTitle: TextView = dialogView.findViewById(R.id.tvDialogTitle)
        val tvSubDetails: TextView = dialogView.findViewById(R.id.tvDialogSubDetails)
        val ivFullScreenshot: ImageView = dialogView.findViewById(R.id.ivFullScreenshot)
        val tvNoImageNotice: TextView = dialogView.findViewById(R.id.tvNoImageNotice)
        val btnClose: Button = dialogView.findViewById(R.id.btnCloseDialog)
        val btnDelete: Button = dialogView.findViewById(R.id.btnDeleteExpenseDialog)

        tvTitle.text = expense.title
        tvSubDetails.text = String.format(Locale.US, "%s • ₹%.2f", expense.category, expense.amount)

        if (expense.imagePath.isNotEmpty()) {
            val bitmap = ImageUtils.decodeSampledBitmapFromFile(expense.imagePath, 1080, 1920)
            if (bitmap != null) {
                ivFullScreenshot.setImageBitmap(bitmap)
                ivFullScreenshot.visibility = View.VISIBLE
                tvNoImageNotice.visibility = View.GONE
            } else {
                ivFullScreenshot.visibility = View.GONE
                tvNoImageNotice.visibility = View.VISIBLE
            }
        } else {
            ivFullScreenshot.visibility = View.GONE
            tvNoImageNotice.visibility = View.VISIBLE
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        btnDelete.setOnClickListener {
            dialog.dismiss()
            confirmDeleteExpense(expense)
        }

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
    }

    private fun confirmDeleteExpense(expense: Expense) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Expense")
            .setMessage("Are you sure you want to delete '${expense.title}' (₹${String.format(Locale.US, "%.2f", expense.amount)})?")
            .setPositiveButton("Delete") { _, _ ->
                val rows = dbHelper.deleteExpense(expense.id)
                if (rows > 0) {
                    if (expense.imagePath.isNotEmpty()) {
                        ImageUtils.deleteFile(expense.imagePath)
                    }
                    Toast.makeText(requireContext(), "Expense deleted", Toast.LENGTH_SHORT).show()
                    (activity as? MainActivity)?.refreshAllData()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        refreshExpenses()
    }
}
