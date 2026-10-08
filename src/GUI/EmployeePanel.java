package GUI;

import DAO.EmployeeDAO;
import model.FullTimeEmployee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EmployeePanel extends JPanel {
    private final EmployeeDAO dao;

    private final JTextField searchField = Theme.field();
    private final JTextField idField = Theme.field();
    private final JTextField nameField = Theme.field();
    private final JTextField addressField = Theme.field();
    private final JTextField phoneField = Theme.field();
    private final JTextField emailField = Theme.field();
    private final JTextField departmentField = Theme.field();
    private final JTextField positionField = Theme.field();
    private final JTextField salaryField = Theme.field();
    private final JTextField allowanceField = Theme.field();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "Name", "Department", "Position", "Type", "Basic Salary", "Allowance", "Total"}
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private final JTable table = new JTable(tableModel);

    public EmployeePanel(EmployeeDAO dao) {
        this.dao = dao;
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BG);
        Theme.styleTable(table);
        setBorder(BorderFactory.createEmptyBorder(25, 28, 25, 28));

        add(createHeader(), BorderLayout.NORTH);
        add(createCenter(), BorderLayout.CENTER);

        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelectedEmployee();
        });
    }

    private JPanel createHeader() {
        JPanel p = new JPanel(new BorderLayout(15, 0));
        p.setOpaque(false);

        JPanel title = new JPanel();
        title.setOpaque(false);
        title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));

        JLabel h = new JLabel("Employees");
        h.setFont(new Font("Segoe UI", Font.BOLD, 27));
        h.setForeground(Theme.TEXT);

        JLabel s = new JLabel("Add, search, update and delete employee records");
        s.setForeground(Theme.MUTED);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        title.add(h);
        title.add(s);

        JPanel search = new JPanel(new BorderLayout(8, 0));
        search.setOpaque(false);
        searchField.setPreferredSize(new Dimension(250, 38));

        JButton find = Theme.button("SEARCH", Theme.BLUE);
        JButton refresh = Theme.button("REFRESH", Theme.NAVY_2);

        find.addActionListener(e -> searchEmployees());
        refresh.addActionListener(e -> refreshData());

        search.add(searchField, BorderLayout.CENTER);
        search.add(find, BorderLayout.EAST);
        search.add(refresh, BorderLayout.WEST);

        p.add(title, BorderLayout.WEST);
        p.add(search, BorderLayout.EAST);
        return p;
    }

    private JSplitPane createCenter() {
        JPanel left = Theme.card();
        left.setLayout(new BorderLayout(0, 10));

        JLabel formTitle = Theme.label("Employee Details");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        addField(form, 0, "Employee ID", idField);
        addField(form, 1, "Full Name", nameField);
        addField(form, 2, "Address", addressField);
        addField(form, 3, "Phone", phoneField);
        addField(form, 4, "Email", emailField);
        addField(form, 5, "Department", departmentField);
        addField(form, 6, "Position", positionField);
        addField(form, 7, "Basic Salary", salaryField);
        addField(form, 8, "Allowance", allowanceField);

        JPanel buttons = new JPanel(new GridLayout(2, 2, 8, 8));
        buttons.setOpaque(false);

        JButton add = Theme.button("ADD", Theme.BLUE);
        JButton update = Theme.button("UPDATE", Theme.GREEN);
        JButton delete = Theme.button("DELETE", Theme.RED);
        JButton clear = Theme.button("CLEAR", Theme.NAVY_2);

        add.addActionListener(e -> addEmployee());
        update.addActionListener(e -> updateEmployee());
        delete.addActionListener(e -> deleteEmployee());
        clear.addActionListener(e -> clearForm());

        buttons.add(add);
        buttons.add(update);
        buttons.add(delete);
        buttons.add(clear);

        left.add(formTitle, BorderLayout.NORTH);
        left.add(form, BorderLayout.CENTER);
        left.add(buttons, BorderLayout.SOUTH);

        JPanel right = Theme.card();
        right.setLayout(new BorderLayout(0, 10));

        JLabel tableTitle = Theme.label("Employee Records");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        right.add(tableTitle, BorderLayout.NORTH);
        right.add(new JScrollPane(table), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.37);
        split.setBorder(null);
        split.setDividerSize(8);
        return split;
    }

    private void addField(JPanel panel, int row, String label, JTextField field) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 3, 4, 8);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.gridy = row; g.weightx = 0;
        panel.add(Theme.label(label), g);

        g.gridx = 1; g.weightx = 1;
        field.setPreferredSize(new Dimension(210, 34));
        panel.add(field, g);
    }

    public void refreshData() {
        try {
            List<Object[]> rows = dao.getAllEmployees();
            fillTable(rows);
        } catch (RuntimeException ex) {
            showDbError(ex);
        }
    }

    private void fillTable(List<Object[]> rows) {
        tableModel.setRowCount(0);
        for (Object[] r : rows) {
            tableModel.addRow(new Object[]{
                    r[0], r[1], r[2], r[3], r[4],
                    String.format("%.2f", ((Number) r[5]).doubleValue()),
                    String.format("%.2f", ((Number) r[6]).doubleValue()),
                    String.format("%.2f", ((Number) r[7]).doubleValue())
            });
        }
    }

    private void searchEmployees() {
        String text = searchField.getText().trim().toLowerCase();
        if (text.isEmpty()) {
            refreshData();
            return;
        }

        try {
            List<Object[]> all = dao.getAllEmployees();
            all.removeIf(r -> !String.valueOf(r[0]).contains(text)
                    && !String.valueOf(r[1]).toLowerCase().contains(text)
                    && !String.valueOf(r[2]).toLowerCase().contains(text)
                    && !String.valueOf(r[3]).toLowerCase().contains(text));
            fillTable(all);
        } catch (RuntimeException ex) {
            showDbError(ex);
        }
    }

    private void loadSelectedEmployee() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;

        int row = table.convertRowIndexToModel(viewRow);
        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());

        try {
            Object[] r = dao.getEmployeeById(id);
            if (r != null) {
                idField.setText(String.valueOf(r[0]));
                nameField.setText(String.valueOf(r[1]));
                addressField.setText(String.valueOf(r[2]));
                phoneField.setText(String.valueOf(r[3]));
                emailField.setText(String.valueOf(r[4]));
                departmentField.setText(String.valueOf(r[5]));
                positionField.setText(String.valueOf(r[6]));
                salaryField.setText(String.valueOf(r[8]));
                allowanceField.setText(String.valueOf(r[9]));
            }
        } catch (RuntimeException ex) {
            showDbError(ex);
        }
    }

    private FullTimeEmployee readForm() {
        if (isBlank(idField) || isBlank(nameField) || isBlank(addressField)
                || isBlank(phoneField) || isBlank(emailField)
                || isBlank(departmentField) || isBlank(positionField)
                || isBlank(salaryField) || isBlank(allowanceField)) {
            throw new IllegalArgumentException("Please fill all fields.");
        }

        int id = Integer.parseInt(idField.getText().trim());
        double salary = Double.parseDouble(salaryField.getText().trim().replace(",", ""));
        double allowance = Double.parseDouble(allowanceField.getText().trim().replace(",", ""));

        return new FullTimeEmployee(
                id, nameField.getText().trim(), addressField.getText().trim(),
                phoneField.getText().trim(), emailField.getText().trim(),
                departmentField.getText().trim(), positionField.getText().trim(),
                salary, allowance
        );
    }

    private boolean isBlank(JTextField f) {
        return f.getText().trim().isEmpty();
    }

    private void addEmployee() {
        try {
            FullTimeEmployee employee = readForm();
            if (dao.addEmployee(employee)) {
                success("Employee added successfully.");
                clearForm();
                refreshData();
            }
        } catch (NumberFormatException ex) {
            error("Employee ID, salary and allowance must be valid numbers.");
        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
        } catch (RuntimeException ex) {
            error(ex.getMessage());
        }
    }

    private void updateEmployee() {
        try {
            FullTimeEmployee employee = readForm();
            if (dao.updateEmployee(employee)) {
                success("Employee updated successfully.");
                refreshData();
            } else {
                error("Employee ID was not found.");
            }
        } catch (NumberFormatException ex) {
            error("Employee ID, salary and allowance must be valid numbers.");
        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
        } catch (RuntimeException ex) {
            error(ex.getMessage());
        }
    }

    private void deleteEmployee() {
        try {
            if (isBlank(idField)) {
                error("Enter or select an Employee ID first.");
                return;
            }

            int id = Integer.parseInt(idField.getText().trim());

            int answer = JOptionPane.showConfirmDialog(
                    this,
                    "Delete employee ID " + id + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (answer == JOptionPane.YES_OPTION) {
                if (dao.deleteEmployee(id)) {
                    success("Employee deleted successfully.");
                    clearForm();
                    refreshData();
                } else {
                    error("Employee ID was not found.");
                }
            }
        } catch (NumberFormatException ex) {
            error("Employee ID must be a whole number.");
        } catch (RuntimeException ex) {
            error(ex.getMessage());
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        addressField.setText("");
        phoneField.setText("");
        emailField.setText("");
        departmentField.setText("");
        positionField.setText("");
        salaryField.setText("");
        allowanceField.setText("");
        table.clearSelection();
        idField.requestFocus();
    }

    private void success(String message) {
        JOptionPane.showMessageDialog(this, message, "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String message) {
        JOptionPane.showMessageDialog(this, message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showDbError(RuntimeException ex) {
        JOptionPane.showMessageDialog(
                this,
                "Database error.\n\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
