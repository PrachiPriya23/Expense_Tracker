# TripExpense - Budget Manager with Payment Proof 💸📱

**TripExpense** is a feature-rich Android application designed to track trip expenses, attach payment proofs (screenshots), monitor category-wise spending via SQLite SQL aggregation, and enforce trip budget alerts.

---

## 🚀 Key Features & Concepts

- **SQLite Database**: Persistent local storage for all trip expenses.
- **Payment Proof Upload**: Attach payment screenshots directly from the device gallery/files. Images are securely stored in internal app storage with downsampled thumbnail generation for smooth scrolling.
- **Expense List View (`ListView`)**: Displays all recorded expenses with title, category, formatted INR amount (`₹`), and screenshot thumbnail.
- **Interactive Details & Deletion**: 
  - **Tap** an expense to view the full-size payment screenshot in a dialog.
  - **Long Press** an expense to delete it with confirmation.
- **SQL Summary & Breakdown**: Uses SQL `SUM` with `GROUP BY category` (`Food`, `Travel`, `Stay`, `Shopping`) to calculate total spending and per-category percentages.
- **Trip Budget Alerts**: Set a custom trip budget. Instantly triggers a Toast warning and warning banner when total spending crosses the budget limit.

---

## 📸 App Screenshots

*(Place your application screenshots in the `screenshots/` directory and update the links below)*

| Expenses List | Add Expense & Proof | Summary Breakdown |
| :---: | :---: | :---: |
| ![Expenses List](screenshots/expenses_list.png) | ![Add Expense](screenshots/add_expense.png) | ![Summary Screen](screenshots/summary.png) |

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin
- **UI Framework**: XML Layouts, Material Design 3 components, `ViewPager2`, `TabLayout`, `ListView`
- **Database**: Android SQLite (`SQLiteOpenHelper`)
- **Storage**: Internal App Storage (`filesDir/payment_proofs`) for robust URI/file persistence
- **Min SDK**: 24+

---

## ⚙️ Setup & Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/TripExpense.git
   ```
2. Open the project in **Android Studio**.
3. Sync Gradle and run the app on an Android emulator or physical device (API 24 or higher).
