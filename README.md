#  Student Grade Tracker – Java GUI Project

A Java Swing-based application to input, manage, and analyze student grades with real-time updates, visualization, and CSV data support.

---

##  Features

- 🔹 Add new students with name and mark
- 🔹 Auto-calculate:
  - Grade (A, B, C, etc.)
  - Remarks (Excellent, Good, etc.)
  - Timestamp (date & time of entry)
- 🔹 Class Report:
  - Average mark
  - Highest & Lowest score
- 🔹 Grade Chart (Bar graph)
- 🔹 Import student data from CSV file
- 🔹 Export all data to CSV for backup or sharing
- 🔹 Edit and Delete student records
- 🔹 Auto-save all data to `data.csv`
- 🔹 Simple login system (username + password)

---

## Technologies Used

- Java (JDK 8 or higher)
- Swing (GUI components)
- JTable, JFileChooser, JOptionPane
- File I/O (CSV reading/writing)
- ArrayLists for dynamic data handling

---

## Enter your Login Credentials

Username: "your name"
Password: "your pass"


You can update these in `AuthDialog.java` easily.

---

##  Project Structure

StudentGradeTracker/
├── Student.java ← Data model for student
├── GradeTrackerGUI.java ← Main GUI application
├── GradeUtils.java ← Grade & remark logic
├── AuthDialog.java ← Login popup window
├── data.csv ← Auto-saved student records
└── grades_export.csv ← Exported data (if exported)


---

## 📝 How to Run

Make sure all files are in one folder, then open terminal or command prompt:

```bash
javac *.java
java GradeTrackerGUI
