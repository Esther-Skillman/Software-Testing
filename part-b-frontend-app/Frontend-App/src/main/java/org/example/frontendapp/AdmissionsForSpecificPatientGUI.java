package org.example.frontendapp;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AdmissionsForSpecificPatientGUI extends JFrame {

    private JTextField textField;
    private JButton button;
    private JLabel patientIdLabel;
    private JLabel currentPatientIdLabel;

    private JTable table;
    private DefaultTableModel tableModel;

    private FrontendController frontendController;

    public AdmissionsForSpecificPatientGUI() {
        setTitle("Admissions for Specific Patient"); // Adding title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel for patient ID input
        JPanel patientIdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        patientIdLabel = new JLabel("Patient ID:");
        patientIdPanel.add(patientIdLabel);
        textField = new JTextField(10);
        textField.setToolTipText("Please enter integer Patient ID e.g. 5"); // Adding tooltip
        patientIdPanel.add(textField);

        // Text field and button for fetching admissions
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        button = new JButton("Find Admissions");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchDataAndPopulateTable();
            }
        });
        searchPanel.add(button);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(patientIdPanel, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Label for displaying current patient ID
        currentPatientIdLabel = new JLabel("Current Patient ID: ");
        currentPatientIdLabel.setFont(currentPatientIdLabel.getFont().deriveFont(16.0f)); // Increase font size
        JPanel middlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Change layout to flow left
        middlePanel.add(currentPatientIdLabel); // Move the label to the middle panel

        tableModel = new DefaultTableModel(new String[]{"Admission ID", "Admission Date", "Discharge Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable cell editing
            }
        };
        table = new JTable(tableModel);
        table.setFocusable(false); // Disable table focus
        table.setRowSelectionAllowed(false); // Disable row selection
        table.setColumnSelectionAllowed(false); // Disable column selection
        table.setGridColor(Color.BLACK); // Change grid color
        table.getTableHeader().setReorderingAllowed(false); // Disable column reordering
        JScrollPane scrollPane = new JScrollPane(table);

        // Add the "Additional Information" row at the start of the table for all columns
        tableModel.addRow(new String[]{"Unique ID for the system", "YYYY-MM-DD 'T' TIME", "YYYY-MM-DD 'T' TIME"});
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer((TableCellRenderer) new ItalicTableCellRenderer());
        }

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(middlePanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void fetchDataAndPopulateTable() {
        String patientIDText = textField.getText();
        try {
            int patientID = Integer.parseInt(patientIDText);
            if (patientID <= 0) {
                // Display error message and clear text field
                JOptionPane.showMessageDialog(this, "Invalid Patient ID. Please enter a valid Patient ID greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Simulate fetching data from backend
            List<String[]> admissionData = FrontendController.fetchDataFromBackend(patientIDText);
            if (admissionData != null) {
                // Update current patient ID label
                currentPatientIdLabel.setText("Current Patient ID: " + patientIDText);

                // Clear existing data in the table
                tableModel.setRowCount(0);

                for (String[] row : admissionData) {
                    tableModel.addRow(row);
                }
            } else {
                // Handle null data
                JOptionPane.showMessageDialog(this, "No admissions found for patient: " + patientIDText, "No Admissions", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            // Handle parsing error
            JOptionPane.showMessageDialog(this, "Invalid Patient ID. Please enter a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Custom cell renderer for rendering italic text
    private class ItalicTableCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Font font = cellComponent.getFont();
            font = font.deriveFont(Font.ITALIC);
            cellComponent.setFont(font);
            return cellComponent;
        }
    }

    public void setFrontendController(FrontendController frontendController) {
        this.frontendController = frontendController;
    }

    public FrontendController getFrontendController() {
        return frontendController;
    }

    public JTextField getTextField() {
        return textField;
    }

    public JButton getButton() {
        return button;
    }

    public JTable getTable() {
        return table;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AdmissionsForSpecificPatientGUI();
            }
        });
    }
}
