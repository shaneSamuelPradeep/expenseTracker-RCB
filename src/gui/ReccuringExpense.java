package gui;

import db.DbConnect;

import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;

public class ReccuringExpense {
    private JTable reccuringTable;

    public void openReccuringExpenseDialog(JFrame parentFrame) {
        JDialog dialog = new JDialog(parentFrame, "Add Recurring Expense", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(7, 2));
        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField();
        JLabel startDateLabel = new JLabel("Start Date:");
        JDateChooser startDateChooser = new JDateChooser();
        JLabel endDateLabel = new JLabel("End Date:");
        JDateChooser endDateChooser = new JDateChooser();
        JLabel expenseTypeLabel = new JLabel("Expense Type:");
        JComboBox<String> expenseTypeComboBox = new JComboBox<>(new String[]{"Category", "Person"});
        JLabel categoryLabel = new JLabel("Category:");
        JTextField categoryField = new JTextField();
        JLabel personLabel = new JLabel("Person:");
        JTextField personField = new JTextField();
        JLabel frequencyLabel = new JLabel("Frequency:");
        JComboBox<String> frequencyComboBox = new JComboBox<>(new String[]{"Weekly", "Monthly"});

        inputPanel.add(amountLabel);
        inputPanel.add(amountField);
        inputPanel.add(startDateLabel);
        inputPanel.add(startDateChooser);
        inputPanel.add(endDateLabel);
        inputPanel.add(endDateChooser);
        inputPanel.add(expenseTypeLabel);
        inputPanel.add(expenseTypeComboBox);
        inputPanel.add(categoryLabel);
        inputPanel.add(categoryField);
        inputPanel.add(personLabel);
        inputPanel.add(personField);
        inputPanel.add(frequencyLabel);
        inputPanel.add(frequencyComboBox);

        categoryLabel.setVisible(false);
        categoryField.setVisible(false);
        personLabel.setVisible(false);
        personField.setVisible(false);

        expenseTypeComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (expenseTypeComboBox.getSelectedItem().toString().equals("Category")) {
                    categoryLabel.setVisible(true);
                    categoryField.setVisible(true);
                    personLabel.setVisible(false);
                    personField.setVisible(false);
                } else {
                    categoryLabel.setVisible(false);
                    categoryField.setVisible(false);
                    personLabel.setVisible(true);
                    personField.setVisible(true);
                }
            }
        });

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                handleReccuringExpenseSubmission(amountField, startDateChooser, endDateChooser, expenseTypeComboBox, categoryField, personField, frequencyComboBox);
            }
        });

        reccuringTable = new JTable(new DefaultTableModel(new Object[][]{}, new String[]{"ID", "Amount", "Start Date", "End Date", "Type", "Category/Person", "Frequency"}));
        JScrollPane tableScrollPane = new JScrollPane(reccuringTable);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                handleReccuringExpenseDeletion();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(deleteButton);

        dialog.add(inputPanel, BorderLayout.NORTH);
        dialog.add(tableScrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        loadReccuringExpenses(); // Load recurring expenses when the dialog is opened

        dialog.setVisible(true);
    }

    private void handleReccuringExpenseSubmission(JTextField amountField, JDateChooser startDateChooser, JDateChooser endDateChooser, JComboBox<String> expenseTypeComboBox, JTextField categoryField, JTextField personField, JComboBox<String> frequencyComboBox) {
        String amountText = amountField.getText();
        int amountValue = Integer.parseInt(amountText);
        java.util.Date startDate = startDateChooser.getDate();
        java.util.Date endDate = endDateChooser.getDate();
        String expenseType = expenseTypeComboBox.getSelectedItem().toString();
        String category = categoryField.getText();
        String person = personField.getText();
        String frequency = frequencyComboBox.getSelectedItem().toString();

        try {
            // Insert the recurring expense into the database
            String query = "INSERT INTO reccuring_expenses (amount, start_date, end_date, expense_type, category, person, frequency) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = DbConnect.c.prepareStatement(query);
            pst.setInt(1, amountValue);
            pst.setDate(2, new java.sql.Date(startDate.getTime()));
            pst.setDate(3, new java.sql.Date(endDate.getTime()));
            pst.setString(4, expenseType);
            pst.setString(5, category);
            pst.setString(6, person);
            pst.setString(7, frequency);
            pst.executeUpdate();

            // Schedule the recurring entries
            scheduleReccuringEntries(amountValue, startDate, endDate, expenseType, category, person, frequency);

            JOptionPane.showMessageDialog(null, "Recurring expense added successfully!");
            loadReccuringExpenses(); // Refresh the table
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    private void handleReccuringExpenseDeletion() {
        int selectedRow = reccuringTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a recurring expense to delete.");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) reccuringTable.getModel();
        int id = (int) model.getValueAt(selectedRow, 0);

        try {
            String query = "DELETE FROM reccuring_expenses WHERE id = ?";
            PreparedStatement pst = DbConnect.c.prepareStatement(query);
            pst.setInt(1, id);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(null, "Recurring expense deleted successfully!");
            loadReccuringExpenses(); // Refresh the table
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    private void loadReccuringExpenses() {
        DefaultTableModel model = (DefaultTableModel) reccuringTable.getModel();
        model.setRowCount(0); // Clear existing data

        try {
            ResultSet rs = DbConnect.st.executeQuery("SELECT * FROM reccuring_expenses");
            while (rs.next()) {
                int id = rs.getInt("id");
                int amount = rs.getInt("amount");
                Date startDate = rs.getDate("start_date");
                Date endDate = rs.getDate("end_date");
                String expenseType = rs.getString("expense_type");
                String categoryOrPerson = expenseType.equals("Category") ? rs.getString("category") : rs.getString("person");
                String frequency = rs.getString("frequency");
                model.addRow(new Object[]{id, amount, startDate, endDate, expenseType, categoryOrPerson, frequency});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    private void scheduleReccuringEntries(int amount, java.util.Date startDate, java.util.Date endDate, String expenseType, String category, String person, String frequency) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        while (!calendar.getTime().after(endDate)) {
            java.sql.Date sqlDate = new java.sql.Date(calendar.getTime().getTime());

            try {
                if (expenseType.equals("Category")) {
                    String query = "INSERT INTO spendings (category, sdate, amount) VALUES (?, ?, ?)";
                    PreparedStatement pst = DbConnect.c.prepareStatement(query);
                    pst.setString(1, category);
                    pst.setDate(2, sqlDate);
                    pst.setInt(3, amount);
                    pst.executeUpdate();
                } else {
                    String query = "INSERT INTO person_spendings (person, pdate, amount) VALUES (?, ?, ?)";
                    PreparedStatement pst = DbConnect.c.prepareStatement(query);
                    pst.setString(1, person);
                    pst.setDate(2, sqlDate);
                    pst.setInt(3, amount);
                    pst.executeUpdate();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex);
            }

            if (frequency.equals("Weekly")) {
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
            } else if (frequency.equals("Monthly")) {
                calendar.add(Calendar.MONTH, 1);
            }
        }
    }
}
