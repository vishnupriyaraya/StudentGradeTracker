// GradeTrackerGUI.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class GradeTrackerGUI extends JFrame {

    private final ArrayList<Student> students = new ArrayList<>();
    private final DefaultTableModel model = new DefaultTableModel(Student.getCSVHeaders(), 0);
    private final JTable table = new JTable(model);
    private final JTextField nameIn = new JTextField();
    private final JTextField markIn = new JTextField();
    private final JTextArea summary = new JTextArea(4, 30);

    public GradeTrackerGUI() {
        if (!AuthDialog.showLogin(this)) {
            JOptionPane.showMessageDialog(this, "Login failed. Exiting.");
            System.exit(0);
        }

        setTitle("Student Grade Tracker");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Input panel
        JPanel input = new JPanel(new GridLayout(2, 4, 8, 8));
        input.add(new JLabel("Student Name:")); input.add(nameIn);
        input.add(new JLabel("Mark (0–100):")); input.add(markIn);

        JButton addBtn = new JButton("Add");
        JButton reportBtn = new JButton("Show Report");
        JButton resetBtn = new JButton("Reset All");
        JButton exportBtn = new JButton("Export CSV");
        input.add(addBtn); input.add(reportBtn); input.add(resetBtn); input.add(exportBtn);

        // Action buttons
        JPanel actions = new JPanel(new GridLayout(1, 4, 8, 8));
        JButton importBtn = new JButton("Import CSV");
        JButton chartBtn  = new JButton("Show Grade Chart");
        JButton editBtn   = new JButton("Edit Selected");
        JButton delBtn    = new JButton("Delete Selected");
        actions.add(importBtn); actions.add(chartBtn); actions.add(editBtn); actions.add(delBtn);

        // Table + Summary
        JScrollPane tablePane = new JScrollPane(table);
        summary.setEditable(false);
        summary.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane summaryPane = new JScrollPane(summary);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tablePane, summaryPane);
        split.setDividerLocation(350);
        split.setResizeWeight(0.8);

        // Add to frame
        add(input, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        // Actions
        addBtn.addActionListener(e -> addStudent());
        reportBtn.addActionListener(e -> showReport());
        resetBtn.addActionListener(e -> resetAll());
        exportBtn.addActionListener(e -> exportCSV());
        importBtn.addActionListener(e -> importCSV());
        chartBtn.addActionListener(e -> showChart());
        editBtn.addActionListener(e -> editStudent());
        delBtn.addActionListener(e -> deleteStudent());

        loadCSV();
        setVisible(true);
    }

    private void addStudent() {
        String name = nameIn.getText().trim();
        String markStr = markIn.getText().trim();

        if (name.isEmpty() || markStr.isEmpty()) {
            msg("Enter both name and mark.");
            return;
        }

        try {
            double mark = Double.parseDouble(markStr);
            if (mark < 0 || mark > 100) throw new NumberFormatException();

            Student s = new Student(name, mark);
            students.add(s);
            model.addRow(s.toCSVRow());
            nameIn.setText(""); markIn.setText("");
            saveCSV();
        } catch (NumberFormatException ex) {
            msg("Mark must be a number between 0 and 100.");
        }
    }

    private void showReport() {
        if (students.isEmpty()) {
            msg("No data available.");
            return;
        }

        double total = 0, max = Double.MIN_VALUE, min = Double.MAX_VALUE;
        for (Student s : students) {
            double m = s.getMark();
            total += m;
            if (m > max) max = m;
            if (m < min) min = m;
        }

        double avg = total / students.size();
        summary.setText(String.format("""
            Class Report:
            -------------
            Average : %.2f
            Highest : %.2f
            Lowest  : %.2f
            """, avg, max, min));
    }

    private void showChart() {
        if (students.isEmpty()) {
            msg("Add some students first.");
            return;
        }

        int[] count = new int[5]; // A, B, C, D, F
        for (Student s : students) {
            switch (s.getGrade()) {
                case "A" -> count[0]++;
                case "B" -> count[1]++;
                case "C" -> count[2]++;
                case "D" -> count[3]++;
                default -> count[4]++;
            }
        }

        JFrame chart = new JFrame("Grade Distribution");
        chart.setSize(500, 400);
        chart.setLocationRelativeTo(this);
        chart.add(new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int barW = 60, gap = 30, baseY = getHeight() - 70;
                int[] heights = count.clone();
                int max = 1;
                for (int n : count) if (n > max) max = n;

                String[] labels = {"A", "B", "C", "D", "F"};
                Color[] colors = {Color.GREEN, Color.BLUE, Color.ORANGE, Color.MAGENTA, Color.RED};

                for (int i = 0; i < heights.length; i++) {
                    int h = (int)((double) heights[i] / max * 200);
                    int x = 50 + i * (barW + gap);
                    g.setColor(colors[i]);
                    g.fillRect(x, baseY - h, barW, h);
                    g.setColor(Color.BLACK);
                    g.drawString(labels[i], x + 20, baseY + 15);
                    g.drawString(String.valueOf(heights[i]), x + 20, baseY - h - 5);
                }
            }
        });
        chart.setVisible(true);
    }

    private void resetAll() {
        if (students.isEmpty()) return;
        if (JOptionPane.showConfirmDialog(this, "Clear all data?", "Confirm", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION) {
            students.clear();
            model.setRowCount(0);
            summary.setText("");
            saveCSV();
        }
    }

    private void exportCSV() {
        try (PrintWriter pw = new PrintWriter("grades_export.csv")) {
            pw.println(String.join(",", Student.getCSVHeaders()));
            for (Student s : students)
                pw.println(String.join(",", s.toCSVRow()));
            msg("Exported to grades_export.csv");
        } catch (Exception e) {
            msg("Export failed.");
        }
    }

    private void importCSV() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File f = chooser.getSelectedFile();

        try (Scanner sc = new Scanner(f)) {
            if (sc.hasNextLine()) sc.nextLine(); // skip header
            while (sc.hasNextLine()) {
                String[] parts = sc.nextLine().split(",", -1);
                if (parts.length == 5) {
                    Student s = new Student(parts[0], Double.parseDouble(parts[1]));
                    students.add(s);
                    model.addRow(s.toCSVRow());
                }
            }
            saveCSV();
            msg("Imported successfully.");
        } catch (Exception e) {
            msg("Import failed.");
        }
    }

    private void editStudent() {
        int row = table.getSelectedRow();
        if (row == -1) {
            msg("Select a student row to edit.");
            return;
        }

        String newName = JOptionPane.showInputDialog(this, "Enter new name:", model.getValueAt(row, 0));
        String newMarkStr = JOptionPane.showInputDialog(this, "Enter new mark:", model.getValueAt(row, 1));

        if (newName == null || newMarkStr == null) return;
        try {
            double newMark = Double.parseDouble(newMarkStr);
            if (newMark < 0 || newMark > 100) throw new NumberFormatException();

            Student s = new Student(newName, newMark);
            students.set(row, s);
            model.setValueAt(s.getName(), row, 0);
            model.setValueAt(s.getMark(), row, 1);
            model.setValueAt(s.getGrade(), row, 2);
            model.setValueAt(s.getRemark(), row, 3);
            model.setValueAt(s.getTimestamp(), row, 4);
            saveCSV();
        } catch (NumberFormatException ex) {
            msg("Invalid mark.");
        }
    }

    private void deleteStudent() {
        int row = table.getSelectedRow();
        if (row == -1) {
            msg("Select a row to delete.");
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "Delete selected student?", "Confirm", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION) {
            students.remove(row);
            model.removeRow(row);
            saveCSV();
        }
    }

    private void loadCSV() {
        File file = new File("data.csv");
        if (!file.exists()) return;

        try (Scanner sc = new Scanner(file)) {
            if (sc.hasNextLine()) sc.nextLine(); // skip header
            while (sc.hasNextLine()) {
                String[] parts = sc.nextLine().split(",", -1);
                if (parts.length == 5) {
                    Student s = new Student(parts[0], Double.parseDouble(parts[1]));
                    students.add(s);
                    model.addRow(s.toCSVRow());
                }
            }
        } catch (Exception ignored) {}
    }

    private void saveCSV() {
        try (PrintWriter pw = new PrintWriter("data.csv")) {
            pw.println(String.join(",", Student.getCSVHeaders()));
            for (Student s : students)
                pw.println(String.join(",", s.toCSVRow()));
        } catch (Exception ignored) {}
    }

    private void msg(String m) {
        JOptionPane.showMessageDialog(this, m);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GradeTrackerGUI::new);
    }
}
