package GUI;

import DAO.PayrollDAO;
import model.Payroll;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PayrollPanel extends JPanel {
    private final PayrollDAO dao;
    private final JTextField payrollIdField = Theme.field();
    private final JTextField employeeIdField = Theme.field();
    private final JTextField monthField = Theme.field();
    private final JTextField deductionsField = Theme.field();
    private final JLabel employeeName = new JLabel("Employee: -");
    private final JLabel employeeType = new JLabel("Type: -");
    private final JLabel grossLabel = new JLabel("Gross Salary: Rs. 0.00");
    private final JLabel netLabel = new JLabel("Net Salary: Rs. 0.00");
    private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "Employee ID", "Name", "Month", "Basic", "Allowance", "Deductions", "Gross", "Net", "Status"}) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public PayrollPanel(PayrollDAO dao) {
        this.dao = dao;
        setLayout(new BorderLayout(0, 15));
        setBackground(Theme.BG);
        Theme.styleTable(table);
        setBorder(BorderFactory.createEmptyBorder(25, 28, 25, 28));

        JLabel title = new JLabel("Payroll Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 27));
        title.setForeground(Theme.TEXT);
        JLabel sub = new JLabel("Calculate salary, deductions and net payroll");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(Theme.MUTED);
        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.add(title); heading.add(sub);
        add(heading, BorderLayout.NORTH);

        JPanel form = Theme.card();
        form.setPreferredSize(new Dimension(365, 0));
        form.setLayout(new BorderLayout(0, 10));
        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        addField(fields, 0, "Payroll ID", payrollIdField);
        addField(fields, 1, "Employee ID", employeeIdField);
        addField(fields, 2, "Month (YYYY-MM)", monthField);
        addField(fields, 3, "Deductions", deductionsField);
        monthField.setText(YearMonth.now().format(monthFormatter));
        deductionsField.setText("0");

        JPanel preview = Theme.card();
        preview.setLayout(new GridLayout(4, 1, 4, 4));
        employeeName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        employeeType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        grossLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        netLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        netLabel.setForeground(Theme.GREEN);
        preview.add(employeeName); preview.add(employeeType); preview.add(grossLabel); preview.add(netLabel);

        JButton calculate = Theme.button("CALCULATE", Theme.BLUE);
        JButton save = Theme.button("SAVE", Theme.GREEN);
        JButton update = Theme.button("UPDATE", Theme.NAVY_2);
        JButton delete = Theme.button("DELETE", Theme.RED);
        JButton clear = Theme.button("CLEAR", Theme.NAVY_2);
        JPanel buttons = new JPanel(new GridLayout(2, 3, 8, 8));
        buttons.setOpaque(false);
        buttons.add(calculate); buttons.add(save); buttons.add(update);
        buttons.add(delete); buttons.add(clear); buttons.add(new JLabel());

        JPanel south = new JPanel(new BorderLayout(0, 10));
        south.setOpaque(false);
        south.add(preview, BorderLayout.NORTH);
        south.add(buttons, BorderLayout.SOUTH);
        form.add(fields, BorderLayout.NORTH);
        form.add(south, BorderLayout.SOUTH);

        JPanel records = Theme.card();
        records.setLayout(new BorderLayout(0, 10));
        JLabel tableTitle = Theme.label("Payroll Records");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        table.setRowHeight(29);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        records.add(tableTitle, BorderLayout.NORTH);
        records.add(new JScrollPane(table), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, form, records);
        split.setResizeWeight(0.30); split.setDividerSize(8); split.setBorder(null);
        add(split, BorderLayout.CENTER);

        calculate.addActionListener(e -> calculate());
        save.addActionListener(e -> save());
        update.addActionListener(e -> update());
        delete.addActionListener(e -> delete());
        clear.addActionListener(e -> clearForm());
        employeeIdField.addActionListener(e -> calculate());
        table.getSelectionModel().addListSelectionListener(e -> loadSelected());
        refreshData();
    }

    private void addField(JPanel panel, int row, String label, JTextField field) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 3, 5, 8); g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.gridy = row; panel.add(Theme.label(label), g);
        g.gridx = 1; g.weightx = 1; field.setPreferredSize(new Dimension(175, 34)); panel.add(field, g);
    }

    private YearMonth readMonth() {
        try { return YearMonth.parse(monthField.getText().trim(), monthFormatter); }
        catch (DateTimeParseException e) { throw new IllegalArgumentException("Use month format YYYY-MM."); }
    }

    private double readDeductions() {
        double d = Double.parseDouble(deductionsField.getText().trim().replace(",", ""));
        if (d < 0) throw new IllegalArgumentException("Deductions cannot be negative.");
        return d;
    }

    private Object[] salary(int employeeId) {
        Object[] s = dao.getSalaryPreview(employeeId);
        if (s == null) throw new IllegalArgumentException("Employee ID does not exist.");
        employeeName.setText("Employee: " + s[0]);
        employeeType.setText("Type: " + s[1]);
        return s;
    }

    private void calculate() {
        try {
            int id = Integer.parseInt(employeeIdField.getText().trim());
            Object[] s = salary(id);
            double gross = ((Number) s[6]).doubleValue();
            double deductions = readDeductions();
            if (deductions > gross) throw new IllegalArgumentException("Deductions cannot be greater than gross salary.");
            grossLabel.setText(String.format("Gross Salary: Rs. %.2f", gross));
            netLabel.setText(String.format("Net Salary: Rs. %.2f", gross - deductions));
        } catch (NumberFormatException e) { error("Employee ID must be a whole number."); }
        catch (RuntimeException e) { error(e.getMessage()); }
    }

    private Payroll readPayroll() {
        int employeeId = Integer.parseInt(employeeIdField.getText().trim());
        YearMonth month = readMonth();
        Object[] s = salary(employeeId);
        double basic = ((Number) s[2]).doubleValue();
        double allowance = ((Number) s[3]).doubleValue();
        double gross = ((Number) s[6]).doubleValue();
        double deductions = readDeductions();
        if (deductions > gross) throw new IllegalArgumentException("Deductions cannot be greater than gross salary.");
        double net = gross - deductions;
        int payrollId = payrollIdField.getText().trim().isEmpty() ? 0 : Integer.parseInt(payrollIdField.getText().trim());
        return new Payroll(payrollId, employeeId, month, basic, allowance, deductions, gross, net, "Generated");
    }

    private void save() {
        try {
            if (dao.addPayroll(readPayroll())) { success("Payroll saved successfully."); refreshData(); clearForm(); }
        } catch (NumberFormatException e) { error("Employee ID, Payroll ID and deductions must be valid numbers."); }
        catch (RuntimeException e) { error(e.getMessage()); }
    }

    private void update() {
        try {
            Payroll p = readPayroll();
            if (p.getPayrollId() <= 0) { error("Select a payroll record first."); return; }
            if (dao.updatePayroll(p)) { success("Payroll updated successfully."); refreshData(); }
            else error("Payroll record not found.");
        } catch (NumberFormatException e) { error("Payroll ID and deductions must be valid numbers."); }
        catch (RuntimeException e) { error(e.getMessage()); }
    }

    private void delete() {
        try {
            if (payrollIdField.getText().trim().isEmpty()) { error("Select a payroll record first."); return; }
            int id = Integer.parseInt(payrollIdField.getText().trim());
            int answer = JOptionPane.showConfirmDialog(this, "Delete payroll ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION && dao.deletePayroll(id)) { success("Payroll deleted successfully."); refreshData(); clearForm(); }
        } catch (NumberFormatException e) { error("Payroll ID must be a whole number."); }
        catch (RuntimeException e) { error(e.getMessage()); }
    }

    private void loadSelected() {
        int row = table.getSelectedRow(); if (row < 0) return;
        int r = table.convertRowIndexToModel(row);
        payrollIdField.setText(model.getValueAt(r, 0).toString());
        employeeIdField.setText(model.getValueAt(r, 1).toString());
        String date = model.getValueAt(r, 3).toString();
        monthField.setText(date.substring(0, 7));
        deductionsField.setText(model.getValueAt(r, 6).toString());
        calculate();
    }

    public void refreshData() {
        try {
            List<Object[]> rows = dao.getAllPayroll();
            model.setRowCount(0);
            for (Object[] r : rows) model.addRow(r);
        } catch (RuntimeException e) { error(e.getMessage()); }
    }

    private void clearForm() {
        payrollIdField.setText(""); employeeIdField.setText("");
        monthField.setText(YearMonth.now().format(monthFormatter)); deductionsField.setText("0");
        employeeName.setText("Employee: -"); employeeType.setText("Type: -");
        grossLabel.setText("Gross Salary: Rs. 0.00"); netLabel.setText("Net Salary: Rs. 0.00");
        table.clearSelection();
    }

    private void success(String m) { JOptionPane.showMessageDialog(this, m, "Success", JOptionPane.INFORMATION_MESSAGE); }
    private void error(String m) { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
}
