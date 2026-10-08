package GUI;

import DAO.PartTimeEmployeeDAO;
import model.PartTimeEmployee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PartTimeEmployeePanel extends JPanel {

    private final PartTimeEmployeeDAO dao;

    private final JTextField idField = Theme.field();
    private final JTextField nameField = Theme.field();
    private final JTextField addressField = Theme.field();
    private final JTextField phoneField = Theme.field();
    private final JTextField emailField = Theme.field();
    private final JTextField departmentField = Theme.field();
    private final JTextField positionField = Theme.field();
    private final JTextField hoursField = Theme.field();
    private final JTextField rateField = Theme.field();

    private final DefaultTableModel model =
            new DefaultTableModel(
                    new Object[][]{},
                    new String[]{
                            "ID",
                            "Name",
                            "Department",
                            "Position",
                            "Hours",
                            "Hourly Rate",
                            "Total Salary"
                    }
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable table = new JTable(model);

    public PartTimeEmployeePanel(PartTimeEmployeeDAO dao) {

        this.dao = dao;

        setLayout(new BorderLayout(0, 15));
        setBackground(Theme.BG);
        Theme.styleTable(table);
        setBorder(
                BorderFactory.createEmptyBorder(
                        25, 28, 25, 28
                )
        );

        JLabel title = new JLabel("Part-Time Employees");
        title.setFont(
                new Font("Segoe UI", Font.BOLD, 27)
        );
        title.setForeground(Theme.TEXT);

        JLabel sub = new JLabel(
                "Manage part-time employee records"
        );
        sub.setFont(
                new Font("Segoe UI", Font.PLAIN, 13)
        );
        sub.setForeground(Theme.MUTED);

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(
                new BoxLayout(
                        heading,
                        BoxLayout.Y_AXIS
                )
        );

        heading.add(title);
        heading.add(sub);

        add(heading, BorderLayout.NORTH);

        JPanel form = Theme.card();
        form.setPreferredSize(
                new Dimension(350, 0)
        );

        form.setLayout(
                new BorderLayout(0, 10)
        );

        JPanel fields =
                new JPanel(new GridBagLayout());

        fields.setOpaque(false);

        addField(fields, 0, "Employee ID", idField);
        addField(fields, 1, "Full Name", nameField);
        addField(fields, 2, "Address", addressField);
        addField(fields, 3, "Phone", phoneField);
        addField(fields, 4, "Email", emailField);
        addField(fields, 5, "Department", departmentField);
        addField(fields, 6, "Position", positionField);
        addField(fields, 7, "Hours Worked", hoursField);
        addField(fields, 8, "Hourly Rate", rateField);

        JButton add =
                Theme.button("ADD", Theme.BLUE);

        JButton update =
                Theme.button("UPDATE", Theme.GREEN);

        JButton delete =
                Theme.button("DELETE", Theme.RED);

        JButton clear =
                Theme.button("CLEAR", Theme.NAVY_2);

        JPanel buttons =
                new JPanel(new GridLayout(2, 2, 8, 8));

        buttons.setOpaque(false);

        buttons.add(add);
        buttons.add(update);
        buttons.add(delete);
        buttons.add(clear);

        form.add(fields, BorderLayout.CENTER);
        form.add(buttons, BorderLayout.SOUTH);

        JPanel records = Theme.card();

        records.setLayout(
                new BorderLayout(0, 10)
        );

        JLabel tableTitle =
                Theme.label("Part-Time Employee Records");

        tableTitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        table.setRowHeight(30);

        table.setAutoCreateRowSorter(true);

        records.add(
                tableTitle,
                BorderLayout.NORTH
        );

        records.add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );

        JSplitPane split =
                new JSplitPane(
                        JSplitPane.HORIZONTAL_SPLIT,
                        form,
                        records
                );

        split.setResizeWeight(0.32);
        split.setDividerSize(8);
        split.setBorder(null);

        add(split, BorderLayout.CENTER);

        add.addActionListener(e -> addEmployee());
        update.addActionListener(e -> updateEmployee());
        delete.addActionListener(e -> deleteEmployee());
        clear.addActionListener(e -> clearForm());

        table.getSelectionModel()
                .addListSelectionListener(e -> loadSelectedEmployee());

        refreshData();
    }

    private void addField(
            JPanel panel,
            int row,
            String label,
            JComponent field) {

        GridBagConstraints g =
                new GridBagConstraints();

        g.insets =
                new Insets(4, 3, 4, 8);

        g.fill =
                GridBagConstraints.HORIZONTAL;

        g.gridx = 0;
        g.gridy = row;

        panel.add(
                Theme.label(label),
                g
        );

        g.gridx = 1;
        g.weightx = 1;

        if (field instanceof JTextField f) {
            f.setPreferredSize(
                    new Dimension(160, 35)
            );
        }

        panel.add(field, g);
    }

    private PartTimeEmployee getEmployeeFromForm() {

        if (idField.getText().trim().isEmpty()
                || nameField.getText().trim().isEmpty()
                || addressField.getText().trim().isEmpty()
                || phoneField.getText().trim().isEmpty()
                || emailField.getText().trim().isEmpty()
                || departmentField.getText().trim().isEmpty()
                || positionField.getText().trim().isEmpty()
                || hoursField.getText().trim().isEmpty()
                || rateField.getText().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Please fill all fields."
            );
        }

        int id =
                Integer.parseInt(
                        idField.getText().trim()
                );

        int hours =
                Integer.parseInt(
                        hoursField.getText().trim()
                );

        double rate =
                Double.parseDouble(
                        rateField.getText().trim()
                );

        if (hours < 0) {
            throw new IllegalArgumentException(
                    "Hours worked cannot be negative."
            );
        }

        if (rate < 0) {
            throw new IllegalArgumentException(
                    "Hourly rate cannot be negative."
            );
        }

        return new PartTimeEmployee(
                id,
                nameField.getText().trim(),
                addressField.getText().trim(),
                phoneField.getText().trim(),
                emailField.getText().trim(),
                departmentField.getText().trim(),
                positionField.getText().trim(),
                hours,
                rate
        );
    }

    private void addEmployee() {

        try {

            PartTimeEmployee employee =
                    getEmployeeFromForm();

            if (dao.addPartTimeEmployee(employee)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Part-time employee added successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                clearForm();
                refreshData();
            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "ID and Hours must be whole numbers.\nHourly Rate must be a number.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (RuntimeException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateEmployee() {

        try {

            PartTimeEmployee employee =
                    getEmployeeFromForm();

            if (dao.updatePartTimeEmployee(employee)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Employee updated successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                clearForm();
                refreshData();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Employee not found.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid numbers.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (RuntimeException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void deleteEmployee() {

        try {

            if (idField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Enter Employee ID.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            int id =
                    Integer.parseInt(
                            idField.getText().trim()
                    );

            int result =
                    JOptionPane.showConfirmDialog(
                            this,
                            "Delete this employee?",
                            "Confirm Delete",
                            JOptionPane.YES_NO_OPTION
                    );

            if (result ==
                    JOptionPane.YES_OPTION) {

                if (dao.deletePartTimeEmployee(id)) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Employee deleted successfully.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    clearForm();
                    refreshData();

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Employee not found.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Employee ID must be a number.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (RuntimeException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void refreshData() {

        try {

            List<Object[]> rows =
                    dao.getAllPartTimeEmployees();

            model.setRowCount(0);

            for (Object[] row : rows) {
                model.addRow(row);
            }

        } catch (RuntimeException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadSelectedEmployee() {

        int row =
                table.getSelectedRow();

        if (row == -1) {
            return;
        }

        int modelRow =
                table.convertRowIndexToModel(row);

        idField.setText(
                model.getValueAt(
                        modelRow, 0
                ).toString()
        );

        nameField.setText(
                model.getValueAt(
                        modelRow, 1
                ).toString()
        );

        departmentField.setText(
                model.getValueAt(
                        modelRow, 2
                ).toString()
        );

        positionField.setText(
                model.getValueAt(
                        modelRow, 3
                ).toString()
        );

        hoursField.setText(
                model.getValueAt(
                        modelRow, 4
                ).toString()
        );

        rateField.setText(
                model.getValueAt(
                        modelRow, 5
                ).toString()
        );
    }

    private void clearForm() {

        idField.setText("");
        nameField.setText("");
        addressField.setText("");
        phoneField.setText("");
        emailField.setText("");
        departmentField.setText("");
        positionField.setText("");
        hoursField.setText("");
        rateField.setText("");

        table.clearSelection();
    }
}