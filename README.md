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
| ![Expenses List](<img width="1080" height="2344" alt="mad 1" src="https://github.com/user-attachments/assets/d2786b56-442c-4d04-8813-60ece570f48e" />
) | ![Add Expense](<img width="1080" height="2344" alt="mad3" src="https://github.com/user-attachments/assets/5a53cfc1-ad99-4503-944f-8c4b8f9921d0" />
) | ![Summary Screen](<<img width="1080" height="2344" alt="mad2" src="https://github.com/user-attachments/assets/4ccfe30a-944e-41b0-b9ec-997476489ccf" />
) |

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin
- **UI Framework**: XML Layouts, Material Design 3 components, `ViewPager2`, `TabLayout`, `ListView`
- **Database**: Android SQLite (`SQLiteOpenHelper`)
- **Storage**: Internal App Storage (`filesDir/payment_proofs`) for robust URI/file persistence
- **Min SDK**: 24+
