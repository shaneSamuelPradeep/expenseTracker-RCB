package gui;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import db.DbConnect;

import java.awt.*;

public class SetLimit {
    private JTable limitTable;

    public void openSetLimitDialog(JFrame parentFrame) {
        JDialog dialog = new JDialog(parentFrame, "Set Limit", true);
        dialog.setSize(400, 400);
        dialog.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        JLabel limitLabel = new JLabel("Limit:");
        JTextField limitField = new JTextField();
        JLabel monthLabel = new JLabel("Month:");
        JComboBox<String> monthComboBox = new JComboBox<>(new String[]{
                "January", "February", "March", "April", "May", "June", 
                "July", "August", "September", "October", "November", "December"});
        JLabel yearLabel = new JLabel("Year:");
        JTextField yearField = new JTextField();

        inputPanel.add(limitLabel);
        inputPanel.add(limitField);
        inputPanel.add(monthLabel);
        inputPanel.add(monthComboBox);
        inputPanel.add(yearLabel);
        inputPanel.add(yearField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(evt -> handleLimitSubmission(limitField, monthComboBox, yearField));

        limitTable = new JTable(new DefaultTableModel(new Object[][] {}, 
                new String[] {"ID", "Limit", "Month", "Year"}));
        JScrollPane tableScrollPane = new JScrollPane(limitTable);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(evt -> handleLimitDeletion());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(deleteButton);

        dialog.add(inputPanel, BorderLayout.NORTH);
        dialog.add(tableScrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        loadLimits();
        dialog.setVisible(true);
    }

    private void handleLimitSubmission(JTextField limitField, JComboBox<String> monthComboBox, JTextField yearField) {
        try {
            int limitValue = Integer.parseInt(limitField.getText());
            String month = monthComboBox.getSelectedItem().toString();
            int year = Integer.parseInt(yearField.getText());

            String query = "INSERT INTO limits (limit_value, month, year) VALUES (?, ?, ?)";
            PreparedStatement pst = DbConnect.c.prepareStatement(query);
            pst.setInt(1, limitValue);
            pst.setString(2, month);
            pst.setInt(3, year);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(null, "Limit added successfully!");
            loadLimits();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    private void handleLimitDeletion() {
        int selectedRow = limitTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a limit to delete.");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) limitTable.getModel();
        int id = (int) model.getValueAt(selectedRow, 0);

        try {
            String query = "DELETE FROM limits WHERE id = ?";
            PreparedStatement pst = DbConnect.c.prepareStatement(query);
            pst.setInt(1, id);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(null, "Limit deleted successfully!");
            loadLimits();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    private void loadLimits() {
        DefaultTableModel model = (DefaultTableModel) limitTable.getModel();
        model.setRowCount(0);

        try {
            ResultSet rs = DbConnect.st.executeQuery("SELECT * FROM limits");
            while (rs.next()) {
                int id = rs.getInt("id");
                int limitValue = rs.getInt("limit_value");
                String month = rs.getString("month");
                int year = rs.getInt("year");
                model.addRow(new Object[]{id, limitValue, month, year});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }
}

