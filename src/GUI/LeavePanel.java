package GUI;

import DAO.LeaveDAO;
import model.LeaveRequest;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class LeavePanel extends JPanel {
    private final LeaveDAO dao;
    private final JTextField employeeIdField = Theme.field();
    private final JTextField startDateField = Theme.field();
    private final JTextField endDateField = Theme.field();
    private final JTextField reasonField = Theme.field();
    private final JTextField balanceEmployeeField = Theme.field();
    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"Annual", "Casual", "Medical"});
    private final JLabel annualBalance = new JLabel("- days");
    private final JLabel casualBalance = new JLabel("- days");
    private final JLabel medicalBalance = new JLabel("- days");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "Employee ID", "Type", "Start", "End", "Days", "Reason", "Status"}) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public LeavePanel(LeaveDAO dao) {
        this.dao = dao;
        setLayout(new BorderLayout(0, 15));
        setBackground(Theme.BG);
        Theme.styleTable(table);
        Theme.styleComboBox(typeBox);
        setBorder(BorderFactory.createEmptyBorder(25, 28, 25, 28));

        JLabel title = new JLabel("Leave Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 27));
        title.setForeground(Theme.TEXT);
        JLabel sub = new JLabel("Request, approve/reject and track employee leave balance");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(Theme.MUTED);

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.add(title);
        heading.add(sub);
        add(heading, BorderLayout.NORTH);

        JPanel form = Theme.card();
        form.setPreferredSize(new Dimension(365, 0));
        form.setLayout(new BorderLayout(0, 10));

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        addField(fields, 0, "Employee ID", employeeIdField);
        addField(fields, 1, "Leave Type", typeBox);
        addField(fields, 2, "Start Date", startDateField);
        addField(fields, 3, "End Date", endDateField);
        addField(fields, 4, "Reason", reasonField);

        startDateField.setText(LocalDate.now().format(formatter));
        endDateField.setText(LocalDate.now().format(formatter));

        JButton apply = Theme.button("APPLY LEAVE", Theme.BLUE);
        JButton approve = Theme.button("APPROVE", Theme.GREEN);
        JButton reject = Theme.button("REJECT", Theme.RED);
        JButton clear = Theme.button("CLEAR", Theme.NAVY_2);

        JPanel buttons = new JPanel(new GridLayout(2, 2, 8, 8));
        buttons.setOpaque(false);
        buttons.add(apply); buttons.add(approve); buttons.add(reject); buttons.add(clear);

        JPanel balance = Theme.card();
        balance.setLayout(new GridLayout(5, 1, 4, 4));
        JLabel balanceTitle = Theme.label("Leave Balance");
        balanceTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JPanel balanceEmployee = new JPanel(new BorderLayout(5, 0));
        balanceEmployee.setOpaque(false);
        balanceEmployee.add(Theme.label("Employee ID"), BorderLayout.WEST);
        balanceEmployee.add(balanceEmployeeField, BorderLayout.CENTER);
        JButton check = Theme.button("CHECK", Theme.BLUE);
        balanceEmployee.add(check, BorderLayout.EAST);
        balance.add(balanceTitle);
        balance.add(balanceEmployee);
        balance.add(annualBalance);
        balance.add(casualBalance);
        balance.add(medicalBalance);
        annualBalance.setText("Annual: - days");
        casualBalance.setText("Casual: - days");
        medicalBalance.setText("Medical: - days");

        form.add(fields, BorderLayout.CENTER);
        form.add(buttons, BorderLayout.SOUTH);

        JPanel records = Theme.card();
        records.setLayout(new BorderLayout(0, 10));
        JLabel tableTitle = Theme.label("Leave Requests");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        table.setRowHeight(29);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        records.add(tableTitle, BorderLayout.NORTH);
        records.add(new JScrollPane(table), BorderLayout.CENTER);
        records.add(balance, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, form, records);
        split.setResizeWeight(0.30);
        split.setDividerSize(8);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);

        apply.addActionListener(e -> applyLeave());
        approve.addActionListener(e -> changeStatus("Approved"));
        reject.addActionListener(e -> changeStatus("Rejected"));
        clear.addActionListener(e -> clearForm());
        check.addActionListener(e -> loadBalance());
        table.getSelectionModel().addListSelectionListener(e -> loadSelected());

        refreshData();
    }

    private void addField(JPanel panel, int row, String label, JComponent field) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 3, 5, 8);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.gridy = row;
        panel.add(Theme.label(label), g);
        g.gridx = 1; g.weightx = 1;
        if (field instanceof JTextField f) f.setPreferredSize(new Dimension(170, 34));
        panel.add(field, g);
    }

    private LeaveRequest readForm() {
        if (employeeIdField.getText().trim().isEmpty() || startDateField.getText().trim().isEmpty()
                || endDateField.getText().trim().isEmpty() || reasonField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID, dates and reason are required.");
        }
        int employeeId = Integer.parseInt(employeeIdField.getText().trim());
        LocalDate start = parseDate(startDateField.getText().trim());
        LocalDate end = parseDate(endDateField.getText().trim());
        if (end.isBefore(start)) throw new IllegalArgumentException("End date cannot be before start date.");
        if (start.getYear() != end.getYear()) throw new IllegalArgumentException("Start and end dates must be in the same year.");
        if (!dao.employeeExists(employeeId)) throw new IllegalArgumentException("Employee ID does not exist.");
        return new LeaveRequest(0, employeeId, typeBox.getSelectedItem().toString(), start, end,
                reasonField.getText().trim(), "Pending");
    }

    private LocalDate parseDate(String value) {
        try { return LocalDate.parse(value, formatter); }
        catch (DateTimeParseException e) { throw new IllegalArgumentException("Use date format YYYY-MM-DD."); }
    }

    private void applyLeave() {
        try {
            if (dao.addLeaveRequest(readForm())) {
                success("Leave request submitted successfully.");
                loadBalance(); refreshData(); clearForm();
            }
        } catch (NumberFormatException e) { error("Employee ID must be a whole number."); }
        catch (RuntimeException e) { error(e.getMessage()); }
    }

    private void changeStatus(String status) {
        int row = table.getSelectedRow();
        if (row < 0) { error("Select a leave request first."); return; }
        int modelRow = table.convertRowIndexToModel(row);
        int leaveId = Integer.parseInt(model.getValueAt(modelRow, 0).toString());
        try {
            if (dao.updateStatus(leaveId, status)) {
                success("Leave request " + status.toLowerCase() + ".");
                refreshData();
                loadBalance();
            }
        } catch (RuntimeException e) { error(e.getMessage()); }
    }

    private void loadSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int r = table.convertRowIndexToModel(row);
        employeeIdField.setText(model.getValueAt(r, 1).toString());
        typeBox.setSelectedItem(model.getValueAt(r, 2).toString());
        startDateField.setText(model.getValueAt(r, 3).toString());
        endDateField.setText(model.getValueAt(r, 4).toString());
        reasonField.setText(model.getValueAt(r, 6).toString());
        balanceEmployeeField.setText(model.getValueAt(r, 1).toString());
        loadBalance();
    }

    private void loadBalance() {
        try {
            if (balanceEmployeeField.getText().trim().isEmpty()) {
                balanceEmployeeField.setText(employeeIdField.getText().trim());
            }
            if (balanceEmployeeField.getText().trim().isEmpty()) return;
            int id = Integer.parseInt(balanceEmployeeField.getText().trim());
            Object[] b = dao.getLeaveBalance(id, LocalDate.now().getYear());
            if (b == null) return;
            annualBalance.setText("Annual: " + b[0] + " days");
            casualBalance.setText("Casual: " + b[1] + " days");
            medicalBalance.setText("Medical: " + b[2] + " days");
        } catch (RuntimeException e) { error(e.getMessage()); }
    }

    public void refreshData() {
        try {
            List<Object[]> rows = dao.getAllLeaveRequests();
            model.setRowCount(0);
            for (Object[] row : rows) model.addRow(row);
        } catch (RuntimeException e) { error(e.getMessage()); }
    }

    private void clearForm() {
        employeeIdField.setText("");
        reasonField.setText("");
        startDateField.setText(LocalDate.now().format(formatter));
        endDateField.setText(LocalDate.now().format(formatter));
        typeBox.setSelectedIndex(0);
        table.clearSelection();
    }

    private void success(String message) { JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE); }
    private void error(String message) { JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE); }
}
