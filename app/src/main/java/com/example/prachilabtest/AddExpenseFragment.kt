package com.example.prachilabtest

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText

class AddExpenseFragment : Fragment() {

    private lateinit var etTitle: TextInputEditText
    private lateinit var etAmount: TextInputEditText
    private lateinit var spCategory: Spinner
    private lateinit var btnUploadScreenshot: Button
    private lateinit var tvScreenshotStatus: TextView
    private lateinit var rlPreviewContainer: RelativeLayout
    private lateinit var ivPreview: ImageView
    private lateinit var btnClearImage: ImageButton
    private lateinit var btnSaveExpense: Button

    private lateinit var dbHelper: ExpenseDatabaseHelper
    private var selectedImageUri: Uri? = null

    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            tvScreenshotStatus.text = "Payment proof attached"
            rlPreviewContainer.visibility = View.VISIBLE
            ivPreview.setImageURI(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.tab_add_expense, container, false)

        dbHelper = ExpenseDatabaseHelper(requireContext())
        etTitle = view.findViewById(R.id.etTitle)
        etAmount = view.findViewById(R.id.etAmount)
        spCategory = view.findViewById(R.id.spCategory)
        btnUploadScreenshot = view.findViewById(R.id.btnUploadScreenshot)
        tvScreenshotStatus = view.findViewById(R.id.tvScreenshotStatus)
        rlPreviewContainer = view.findViewById(R.id.rlPreviewContainer)
        ivPreview = view.findViewById(R.id.ivPreview)
        btnClearImage = view.findViewById(R.id.btnClearImage)
        btnSaveExpense = view.findViewById(R.id.btnSaveExpense)

        // Setup Spinner
        val categories = arrayOf("Food", "Travel", "Stay", "Shopping")
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )
        spCategory.adapter = spinnerAdapter

        btnUploadScreenshot.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        btnClearImage.setOnClickListener {
            resetImageSelection()
        }

        btnSaveExpense.setOnClickListener {
            saveExpense()
        }

        return view
    }

    private fun resetImageSelection() {
        selectedImageUri = null
        tvScreenshotStatus.text = "No file attached"
        rlPreviewContainer.visibility = View.GONE
        ivPreview.setImageDrawable(null)
    }

    private fun saveExpense() {
        val title = etTitle.text?.toString()?.trim() ?: ""
        val amountStr = etAmount.text?.toString()?.trim() ?: ""
        val category = spCategory.selectedItem?.toString() ?: "Food"

        if (title.isEmpty()) {
            etTitle.error = "Please enter an expense title"
            etTitle.requestFocus()
            return
        }

        if (amountStr.isEmpty()) {
            etAmount.error = "Please enter an amount"
            etAmount.requestFocus()
            return
        }

        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            etAmount.error = "Please enter a valid positive amount"
            etAmount.requestFocus()
            return
        }

        // Copy image to internal storage if selected
        var imagePath = ""
        selectedImageUri?.let { uri ->
            val savedPath = ImageUtils.copyUriToInternalStorage(requireContext(), uri)
            if (savedPath != null) {
                imagePath = savedPath
            }
        }

        val expense = Expense(
            title = title,
            amount = amount,
            category = category,
            imagePath = imagePath
        )

        val rowId = dbHelper.insertExpense(expense)
        if (rowId != -1L) {
            Toast.makeText(requireContext(), "Expense saved successfully!", Toast.LENGTH_SHORT).show()
            clearForm()
            (activity as? MainActivity)?.let { mainActivity ->
                mainActivity.refreshAllData()
                mainActivity.switchToTab(0)
            }
        } else {
            Toast.makeText(requireContext(), "Failed to save expense", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearForm() {
        etTitle.setText("")
        etAmount.setText("")
        spCategory.setSelection(0)
        resetImageSelection()
    }
}
