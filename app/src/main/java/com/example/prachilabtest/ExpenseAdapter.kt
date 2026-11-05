package com.example.prachilabtest

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.imageview.ShapeableImageView
import java.util.Locale

class ExpenseAdapter(
    context: Context,
    private val expenses: List<Expense>
) : ArrayAdapter<Expense>(context, 0, expenses) {

    private class ViewHolder(view: View) {
        val ivThumbnail: ShapeableImageView = view.findViewById(R.id.ivExpenseThumbnail)
        val tvTitle: TextView = view.findViewById(R.id.tvExpenseTitle)
        val tvCategory: TextView = view.findViewById(R.id.tvExpenseCategory)
        val ivCategoryIcon: ImageView = view.findViewById(R.id.ivCategoryIcon)
        val tvAmount: TextView = view.findViewById(R.id.tvExpenseAmount)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val expense = getItem(position)
        if (expense != null) {
            holder.tvTitle.text = expense.title
            holder.tvCategory.text = expense.category
            holder.tvAmount.text = String.format(Locale.US, "₹%.2f", expense.amount)

            // Category Icon
            val iconRes = when (expense.category.lowercase(Locale.ROOT)) {
                "food" -> R.drawable.ic_food
                "travel" -> R.drawable.ic_travel
                "stay" -> R.drawable.ic_stay
                "shopping" -> R.drawable.ic_shopping
                else -> R.drawable.ic_receipt_placeholder
            }
            holder.ivCategoryIcon.setImageResource(iconRes)

            // Scaled thumbnail
            if (expense.imagePath.isNotEmpty()) {
                val thumbnail = ImageUtils.decodeSampledBitmapFromFile(expense.imagePath, 120, 120)
                if (thumbnail != null) {
                    holder.ivThumbnail.setImageBitmap(thumbnail)
                    holder.ivThumbnail.scaleType = ImageView.ScaleType.CENTER_CROP
                } else {
                    holder.ivThumbnail.setImageResource(R.drawable.ic_receipt_placeholder)
                    holder.ivThumbnail.scaleType = ImageView.ScaleType.CENTER_INSIDE
                }
            } else {
                holder.ivThumbnail.setImageResource(R.drawable.ic_receipt_placeholder)
                holder.ivThumbnail.scaleType = ImageView.ScaleType.CENTER_INSIDE
            }
        }

        return view
    }
}
